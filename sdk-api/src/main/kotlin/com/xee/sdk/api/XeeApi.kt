/*
 * Copyright 2017 Xee
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.sdk.api

import com.google.gson.GsonBuilder
import com.xee.sdk.api.endpoint.TripsEndpoint
import com.xee.sdk.api.endpoint.UsersEndpoint
import com.xee.sdk.api.endpoint.VehiclesEndpoint
import com.xee.sdk.api.model.*
import com.xee.sdk.core.auth.XeeAuth
import com.xee.sdk.core.common.HttpHeaders
import com.xee.sdk.core.common.XeeEnv
import com.xee.sdk.core.common.DateDeserializer
import com.xee.sdk.core.common.interceptor.LogRequestInterceptor
import com.xee.sdk.core.common.interceptor.ApiInterceptor
import com.xee.sdk.core.common.model.buildThrowableError
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * XeeApi class to call API endpoints
 * @param environment the [XeeEnv] environment
 * @param enableLog true to enable logs, otherwise false
 * @author Julien Cholin
 * @since 4.0.0
 */
class XeeApi @JvmOverloads constructor(environment: XeeEnv, private val enableLog: Boolean = false) {

    companion object {
        const val TAG = "XeeApi"
        const val ROUTE_BASE = "https://fleet.rezo.loco.red/"
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        private val LOG_INTERCEPTOR = LogRequestInterceptor()
        @JvmField
        val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT, Locale.US)
        private val CONNECTION_EXCEPTION = IllegalStateException("You must connect the user before anything")
        private lateinit var CONVERTER_FACTORY: Converter.Factory
        private var ENABLE_LOG: Boolean = false
    }

    /**
     * Some params needed to be passed in requests
     */
    object Params {
        const val ME = "me"
        const val FROM = "from"
        const val TO = "to"
        const val LIMIT = "limit"
        const val SIGNALS = "signals"
        const val DEVICE_ID = "deviceId"
        const val DEVICE_PIN = "devicePin"
    }

    private var xeeEnv: XeeEnv = environment
    private var usersEndpoint: UsersEndpoint? = null
    private var vehiclesEndpoint: VehiclesEndpoint? = null
    private var tripsEndpoint: TripsEndpoint? = null

    init {
        ENABLE_LOG = enableLog
        CONVERTER_FACTORY = GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer(ENABLE_LOG)).create())
        initApiEndpoint()
    }

    /**
     * Init the API endpoint
     */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun initApiEndpoint() {
        val apiClientBuilder = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val token = xeeEnv.tokenStorage?.get()
                    val requestBuilder = chain.request().newBuilder()
                            .header(HttpHeaders.HEADER_AUTHORIZATION, HttpHeaders.HEADER_BEARER + token?.accessToken)
                    chain.proceed(requestBuilder.build())
                }
                .addInterceptor(xeeEnv.tokenStorage?.let { ApiInterceptor(xeeEnv, it) })
                .connectTimeout(xeeEnv.connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(xeeEnv.readTimeout, TimeUnit.MILLISECONDS)

        // If the SDK is in debug mode, we log all the requests and the responses sent
        if (ENABLE_LOG) apiClientBuilder.addInterceptor(LOG_INTERCEPTOR)

        // Build the retrofit interface and the API services
        val apiRetrofit = Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, ROUTE_BASE, xeeEnv.environment))
                .client(apiClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CONVERTER_FACTORY)
                .build()

        usersEndpoint = apiRetrofit.create<UsersEndpoint>(UsersEndpoint::class.java)
        vehiclesEndpoint = apiRetrofit.create<VehiclesEndpoint>(VehiclesEndpoint::class.java)
        tripsEndpoint = apiRetrofit.create<TripsEndpoint>(TripsEndpoint::class.java)
    }

    /**
     * @return if the user is logged
     */
    fun isLogged(): Boolean = XeeAuth.logged

    //region [USER]
    /**
     * Returns the current user authenticated
     */
    fun getUser(): Observable<User> = handleObservableError(usersEndpoint?.getUser())

    /**
     * Returns a user corresponding to specified id
     * @param userId uuid of the user, me is also acceptable
     */
    fun getUser(userId: String): Observable<User> =
            handleObservableError(usersEndpoint?.getUser(userId))

    /**
     * Update the current user
     * @param userToUpdate the [User] to update
     */
    fun updateUser(userToUpdate: User): Observable<User> =
            handleObservableError(usersEndpoint?.updateUser(Params.ME, userToUpdate))

    /**
     * Returns vehicles corresponding to the current user authenticated
     */
    fun getUserVehicles(): Observable<List<Vehicle>> =
            handleObservableError(usersEndpoint?.getUserVehicles(Params.ME))

    /**
     * Returns vehicles corresponding to specified user id (me is also acceptable)
     * @param userId uuid of the user, me is also acceptable
     */
    fun getUserVehicles(userId: String): Observable<List<Vehicle>> =
            handleObservableError(usersEndpoint?.getUserVehicles(userId))

    /**
     * Set a vehicle for a user with a specified device Id and pin code
     * @param deviceId the id of [Device]
     * @param pin the pin of [Device]
     */
    fun associateVehicle(deviceId: String, pin: String): Observable<Vehicle> {
        val deviceInfo = mutableMapOf<String, Any>()
        deviceInfo[Params.DEVICE_ID] = deviceId
        deviceInfo[Params.DEVICE_PIN] = pin
        return handleObservableError(usersEndpoint?.associateVehicle(Params.ME, deviceInfo))
    }

    /**
     * Delete the pairing between a vehicle and a device
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun dissociateVehicle(vehicleId: String): Completable =
            handleCompletableError(vehiclesEndpoint?.dissociateVehicle(vehicleId))

    /**
     * Returns authorizations corresponding to the current user authenticated
     */
    fun getAuthorizations(): Observable<List<Authorization>> =
            handleObservableError(usersEndpoint?.getAuthorizations(Params.ME))

    /**
     * Returns authorizations corresponding to specified user id
     * @param userId the uuid of the [User]
     */
    fun getAuthorizations(userId: String): Observable<List<Authorization>> =
            handleObservableError(usersEndpoint?.getAuthorizations(userId))

    /**
     * Revokes the specified authorization
     * @param authorizationId the uuid of the [Authorization]
     */
    fun revokeAuthorization(authorizationId: String): Completable =
            handleCompletableError(usersEndpoint?.revokeAuthorization(authorizationId))
    //endregion

    //region [VEHICLES]
    /**
     * Returns a vehicle corresponding to specified id
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun getVehicle(vehicleId: String): Observable<Vehicle> =
            handleObservableError(vehiclesEndpoint?.getVehicle(vehicleId))

    /**
     * Retrieve device data corresponding to specified vehicle id
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehicleDeviceData(vehicleId: String, optionalParameters: Map<String, Any>): Observable<List<Any>> =
            handleObservableError(vehiclesEndpoint?.getVehicleDeviceData(vehicleId, optionalParameters))

    /**
     * Retrieve device data corresponding to specified vehicle id
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehicleDeviceData(vehicleId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Any>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(vehiclesEndpoint?.getVehicleDeviceData(vehicleId, optionalParameters))
    }

    /**
     * Returns vehicles privacies
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehiclePrivacies(vehicleId: String, optionalParameters: Map<String, Any>): Observable<List<Privacy>> =
            handleObservableError(vehiclesEndpoint?.getVehiclePrivacies(vehicleId, optionalParameters))

    /**
     * Returns vehicles privacies
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehiclePrivacies(vehicleId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Privacy>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(vehiclesEndpoint?.getVehiclePrivacies(vehicleId, optionalParameters))
    }

    /**
     * Returns trips corresponding to specified vehicle id
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun getVehicleTrips(vehicleId: String): Observable<List<Trip>> =
            handleObservableError(vehiclesEndpoint?.getVehicleTrips(vehicleId))

    /**
     * Update a vehicle with a specified id
     * @param vehicleId the uuid of the [Vehicle]
     * @param vehicleToUpdate the [Vehicle] to update
     */
    fun updateVehicle(vehicleId: String, vehicleToUpdate: Vehicle): Observable<Vehicle> =
            handleObservableError(vehiclesEndpoint?.updateVehicle(vehicleId, vehicleToUpdate))

    /**
     * Creates a new privacy session on a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun enablePrivacy(vehicleId: String): Observable<Privacy> =
            handleObservableError(vehiclesEndpoint?.enablePrivacy(vehicleId))

    /**
     * Stop an existing privacy session
     * @param the uuid of the [Privacy]
     */
    fun disablePrivacy(privacyId: String): Observable<Privacy> =
            handleObservableError(vehiclesEndpoint?.disablePrivacy(privacyId))

    /**
     * Retrieves signals for a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehicleSignals(vehicleId: String, optionalParameters: Map<String, Any>): Observable<List<Signal>> =
            handleObservableError(vehiclesEndpoint?.getVehicleSignals(vehicleId, optionalParameters))

    /**
     * Retrieves signals for a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param signals the list of [Signal] to filter
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehicleSignals(vehicleId: String, from: Date? = null, to: Date? = null, signals: String? = null, limit: Int? = null): Observable<List<Signal>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        if (signals != null) optionalParameters[Params.SIGNALS] = signals
        return handleObservableError(vehiclesEndpoint?.getVehicleSignals(vehicleId, optionalParameters))
    }

    /**
     * Retrieves locations for a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehicleLocations(vehicleId: String, optionalParameters: Map<String, Any>): Observable<List<Location>> =
            handleObservableError(vehiclesEndpoint?.getVehicleLocations(vehicleId, optionalParameters))

    /**
     * Retrieves locations for a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehicleLocations(vehicleId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Location>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(vehiclesEndpoint?.getVehicleLocations(vehicleId, optionalParameters))
    }

    /**
     * Retrieves the accelerometers data of a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehicleAccelerometers(vehicleId: String, optionalParameters: Map<String, Any>): Observable<List<Accelerometer>> =
            handleObservableError(vehiclesEndpoint?.getVehicleAccelerometers(vehicleId, optionalParameters))

    /**
     * Retrieves the accelerometers data of a vehicle
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehicleAccelerometers(vehicleId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Accelerometer>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(vehiclesEndpoint?.getVehicleAccelerometers(vehicleId, optionalParameters))
    }

    /**
     * Returns the vehicle status of the vehicle
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun getVehicleStatus(vehicleId: String): Observable<Status> =
            handleObservableError(vehiclesEndpoint?.getVehicleStatus(vehicleId))
    //endregion

    //region [TRIPS]
    /**
     * Returns trip corresponding to specified trip id
     * @param tripId the uuid of the [Trip]
     */
    fun getTrip(tripId: String): Observable<Trip> =
            handleObservableError(tripsEndpoint?.getTrip(tripId))

    /**
     * Returns trips signals by redirecting to the signals api
     * @param tripId the uuid of the [Trip]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getTripSignals(tripId: String, optionalParameters: Map<String, Any>): Observable<List<Signal>> =
            handleObservableError(tripsEndpoint?.getTripSignals(tripId, optionalParameters))

    /**
     * Returns trips signals by redirecting to the signals api
     * @param tripId the uuid of the [Trip]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getTripSignals(tripId: String, from: Date? = null, to: Date? = null, signals: String? = null, limit: Int? = null): Observable<List<Signal>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        if (signals != null) optionalParameters[Params.SIGNALS] = signals
        return handleObservableError(tripsEndpoint?.getTripSignals(tripId, optionalParameters))
    }

    /**
     * Returns trips locations by redirecting to the signals api
     * @param tripId the uuid of the [Trip]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getTripLocations(tripId: String, optionalParameters: Map<String, Any>): Observable<List<Location>> =
            handleObservableError(tripsEndpoint?.getTripLocations(tripId, optionalParameters))

    /**
     * Returns locations signals by redirecting to the signals api
     * @param tripId the uuid of the [Trip]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getTripLocations(tripId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Location>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if (limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(tripsEndpoint?.getTripLocations(tripId, optionalParameters))
    }
    //endregion

    //region [ERROR]
    /**
     * Handle error from an [Observable]
     */
    private fun <T> handleObservableError(o: Observable<T>?): Observable<T> {
        return if (!isLogged()) {
            Observable.error(CONNECTION_EXCEPTION)
        } else {
            o!!.onErrorResumeNext { t: Throwable ->
                Observable.error(t.buildThrowableError())
            }
        }
    }

    /**
     * Handle error from an [Completable]
     */
    private fun handleCompletableError(o: Completable?): Completable {
        return if (!isLogged()) {
            Completable.error(CONNECTION_EXCEPTION)
        } else {
            o!!.onErrorResumeNext { t: Throwable ->
                Completable.error(t.buildThrowableError())
            }
        }
    }
    //endregion
}
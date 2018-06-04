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

package com.xee.sdk.fleet

import com.google.gson.GsonBuilder
import com.xee.sdk.core.auth.XeeAuth
import com.xee.sdk.core.common.DateDeserializer
import com.xee.sdk.core.common.HttpHeaders
import com.xee.sdk.core.common.XeeEnv
import com.xee.sdk.core.common.interceptor.LogRequestInterceptor
import com.xee.sdk.core.common.interceptor.ApiInterceptor
import com.xee.sdk.core.common.model.buildThrowableError
import com.xee.sdk.fleet.endpoint.FleetsEndpoint
import com.xee.sdk.fleet.endpoint.TripsEndpoint
import com.xee.sdk.fleet.endpoint.VehiclesEndpoint
import com.xee.sdk.fleet.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * XeeApi class to call FLEET endpoints
 * @param environment the [XeeEnv] environment
 * @param enableLog true to enable logs, otherwise false
 * @author Julien Cholin
 * @since 4.0.0
 */
class XeeFleet @JvmOverloads constructor(environment: XeeEnv, private val enableLog: Boolean = false) {

    companion object {
        const val TAG = "Xee"
        const val ROUTE_BASE = "https://%s.xee.com/v4/"
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        private val LOG_INTERCEPTOR = LogRequestInterceptor()
        private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT, Locale.US)
        private val CONNECTION_EXCEPTION = IllegalStateException("You must connect the user before anything")
        private lateinit var CONVERTER_FACTORY:Converter.Factory
        private var ENABLE_LOG:Boolean = false
    }

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
    private var fleetsEndpoint: FleetsEndpoint? = null
    private var vehiclesEndpoint: VehiclesEndpoint? = null
    private var tripsEndpoint: TripsEndpoint? = null

    init {
        ENABLE_LOG = enableLog
        CONVERTER_FACTORY = GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer(ENABLE_LOG)).create())
        initFleetApiEndpoint()
    }

    /**
     * Init the FLEET endpoint
     */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun initFleetApiEndpoint() {
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

        fleetsEndpoint = apiRetrofit.create<FleetsEndpoint>(FleetsEndpoint::class.java)
        vehiclesEndpoint = apiRetrofit.create<VehiclesEndpoint>(VehiclesEndpoint::class.java)
    }

    /**
     * @return if the user is logged
     */
    fun isLogged(): Boolean = XeeAuth.logged

    //region [USER]
    /**
     * Get fleets of the current user authenticated
     */
    fun getMyFleets():Observable<List<Fleet>> = handleObservableError(fleetsEndpoint?.getMyFleets())

    /**
     * Returns fleet corresponding to specified fleet id
     * @param fleetId the uuid of the [Fleet]
     */
    fun getFleetDrivers(fleetId:String): Observable<List<User>> =
            handleObservableError(fleetsEndpoint?.getFleetDrivers(fleetId))

    /**
     * Returns the user corresponding to specified fleet id
     * @param fleetId the uuid of the [Fleet]
     */
    fun getDriver(fleetId:String): Observable<User> =
            handleObservableError(fleetsEndpoint?.getDriver(fleetId))

    /**
     * Returns vehicles of the fleet corresponding to specified fleet id
     * @param fleetId the uuid of the [Fleet]
     */
    fun getFleetVehicles(fleetId: String):Observable<List<Vehicle>> =
            handleObservableError(fleetsEndpoint?.getFleetVehicles(fleetId))

    /**
     * Returns trip corresponding to specified trip id
     * @param tripId the uuid of the [Trip]
     */
    fun getTripBehaviors(tripId:String):Observable<Trip> =
            handleObservableError(tripsEndpoint?.getTripBehaviors(tripId))

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



    /**
     * Returns trips corresponding to specified vehicle id
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */


    @JvmOverloads
    fun getVehicleTrips(vehicleId: String, from: Date? = null, to: Date? = null): Observable<List<Trip>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if (from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if (to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        return handleObservableError(vehiclesEndpoint?.getVehicleTrips(vehicleId, optionalParameters))
    }
    /**
     * Returns loans of a vehicle of the fleet
     * @param fleetId the uuid of the [Fleet]
     * @param vehicleId the uuid of the [Vehicle]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getVehicleLoans(fleetId: String, vehicleId:String, optionalParameters: Map<String, Any>):Observable<List<Loan>> =
            handleObservableError(fleetsEndpoint?.getVehicleLoans(fleetId, vehicleId, optionalParameters))

    /**
     * Returns loans of a vehicle of the fleet
     * @param fleetId the uuid of the [Fleet]
     * @param vehicleId the uuid of the [Vehicle]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getVehicleLoans(fleetId: String, vehicleId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Loan>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if(from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if(to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if(limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(fleetsEndpoint?.getVehicleLoans(fleetId, vehicleId, optionalParameters))
    }

    /**
     * Returns loans of a driver of the fleet
     * @param fleetId the uuid of the [Fleet]
     * @param driverId the uuid of the [User]
     * @param optionalParameters an optional [Map] of parameters
     */
    fun getDriverLoans(fleetId: String, driverId:String, optionalParameters: Map<String, Any>):Observable<List<Loan>> =
            handleObservableError(fleetsEndpoint?.getDriverLoans(fleetId, driverId, optionalParameters))

    /**
     * Returns loans of a driver of the fleet
     * @param fleetId the uuid of the [Fleet]
     * @param driverId the uuid of the [User]
     * @param from the beginning date of the search
     * @param to the ending date of the search
     * @param limit the limit of the search
     */
    @JvmOverloads
    fun getDriverLoans(fleetId: String, driverId: String, from: Date? = null, to: Date? = null, limit: Int? = null): Observable<List<Loan>> {
        val optionalParameters = mutableMapOf<String, Any>()
        if(from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if(to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if(limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(fleetsEndpoint?.getDriverLoans(fleetId, driverId, optionalParameters))
    }

    /**
     * Start a loan of a vehicle
     * @param fleetId the uuid of the [Fleet]
     * @param vehicleId the uuid of the [Vehicle]
     * @param driverId the uuid of the [User]
     */
    fun startLoan(fleetId: String, vehicleId: String, driverId: String):Observable<Loan> {
        val bodyMap = mutableMapOf<String, Any>()
        bodyMap["userId"] = driverId
        return handleObservableError(fleetsEndpoint?.startLoan(fleetId, vehicleId, bodyMap))
    }

    /**
     * Stop a loan of a vehicle
     * @param fleetId the uuid of the [Fleet]
     * @param loanId the uuid of the [Loan]
     */
    fun endLoan(fleetId: String, loanId:String):Completable =
            handleCompletableError(fleetsEndpoint?.endLoan(fleetId, loanId))

    //endregion

    //region [VEHICLE]
    /**
     * Returns the vehicle status of the vehicle
     * @param vehicleId the uuid of the [Vehicle]
     */
    fun getVehicleStatus(vehicleId: String): Observable<Status> =
            handleObservableError(vehiclesEndpoint?.getVehicleStatus(vehicleId))

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
        if(from != null) optionalParameters[Params.FROM] = DATE_FORMATTER.format(from)
        if(to != null) optionalParameters[Params.TO] = DATE_FORMATTER.format(to)
        if(limit != null) optionalParameters[Params.LIMIT] = limit
        return handleObservableError(vehiclesEndpoint?.getVehiclePrivacies(vehicleId, optionalParameters))
    }

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
    fun disablePrivacy(privacyId: String): Observable<Privacy> {
        return handleObservableError(vehiclesEndpoint?.disablePrivacy(privacyId))
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
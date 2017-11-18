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

package com.xee.sdk.api.endpoint

import com.xee.sdk.api.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

/**
 * List of [Vehicle] endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface VehiclesEndpoint {

    object Routes {
        const val VEHICLE = "vehicles/{${Parameters.VEHICLE_ID}}"
        const val VEHICLE_PRIVACIES = "vehicles/{${Parameters.VEHICLE_ID}}/privacies"
        const val VEHICLE_TRIPS = "vehicles/{${Parameters.VEHICLE_ID}}/trips"
        const val VEHICLE_SIGNALS = "vehicles/{${Parameters.VEHICLE_ID}}/signals2"
        const val VEHICLE_STATUS = "vehicles/{${Parameters.VEHICLE_ID}}/status"
        const val VEHICLE_LOCATIONS = "vehicles/{${Parameters.VEHICLE_ID}}/locations"
        const val VEHICLE_ACCELEROMETERS = "vehicles/{${Parameters.VEHICLE_ID}}/accelerometers"
        const val VEHICLE_DEVICE_DATA = "vehicles/{${Parameters.VEHICLE_ID}}/device/data"
        const val VEHICLE_DISABLE_PRIVACY = "privacies/{${Parameters.PRIVACY_ID}}"
        const val DISSOCIATE_VEHICLE = "vehicles/{${Parameters.VEHICLE_ID}}/device"
    }

    object Parameters {
        const val VEHICLE_ID = "vehicleId"
        const val PRIVACY_ID = "privacyId"
    }

    @GET(Routes.VEHICLE)
    fun getVehicle(@Path(Parameters.VEHICLE_ID) vehicleId:String): Observable<Vehicle>

    @GET(Routes.VEHICLE_PRIVACIES)
    fun getVehiclePrivacies(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Privacy>>

    @GET(Routes.VEHICLE_TRIPS)
    fun getVehicleTrips(@Path(Parameters.VEHICLE_ID) vehicleId:String): Observable<List<Trip>>

    @GET(Routes.VEHICLE_SIGNALS)
    fun getVehicleSignals(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Signal>>

    @GET(Routes.VEHICLE_STATUS)
    fun getVehicleStatus(@Path(Parameters.VEHICLE_ID) vehicleId:String): Observable<Status>

    @GET(Routes.VEHICLE_LOCATIONS)
    fun getVehicleLocations(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Location>>

    @GET(Routes.VEHICLE_ACCELEROMETERS)
    fun getVehicleAccelerometers(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Accelerometer>>

    @GET(Routes.VEHICLE_DEVICE_DATA)
    fun getVehicleDeviceData(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Any>>

    @PATCH(Routes.VEHICLE)
    fun updateVehicle(@Path(Parameters.VEHICLE_ID) vehicleId:String, @Body vehicleToUpdate: Vehicle):Observable<Vehicle>

    @POST(Routes.VEHICLE_PRIVACIES)
    fun enablePrivacy(@Path(Parameters.VEHICLE_ID) vehicleId:String):Observable<Privacy>

    @PUT(Routes.VEHICLE_DISABLE_PRIVACY)
    fun disablePrivacy(@Path(Parameters.PRIVACY_ID) privacyId:String):Observable<Privacy>

    @DELETE(Routes.DISSOCIATE_VEHICLE)
    fun dissociateVehicle(@Path(Parameters.VEHICLE_ID) vehicleId: String): Completable
}
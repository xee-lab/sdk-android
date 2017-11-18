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

package com.xee.sdk.fleet.endpoint

import com.xee.sdk.fleet.model.Privacy
import com.xee.sdk.fleet.model.Status
import io.reactivex.Observable
import retrofit2.http.*

/**
 * List of [com.xee.sdk.fleet.model.Vehicle] endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface VehiclesEndpoint {

    object Routes {
        const val VEHICLE_PRIVACIES = "vehicles/{${Parameters.VEHICLE_ID}}/privacies"
        const val VEHICLE_STATUS = "vehicles/{${Parameters.VEHICLE_ID}}/status"
        const val VEHICLE_DISABLE_PRIVACY = "privacies/{${Parameters.PRIVACY_ID}}"
    }

    object Parameters {
        const val VEHICLE_ID = "vehicleId"
        const val PRIVACY_ID = "privacyId"
    }

    @GET(Routes.VEHICLE_PRIVACIES)
    fun getVehiclePrivacies(@Path(Parameters.VEHICLE_ID) vehicleId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Privacy>>

    @GET(Routes.VEHICLE_STATUS)
    fun getVehicleStatus(@Path(Parameters.VEHICLE_ID) vehicleId:String): Observable<Status>

    @POST(Routes.VEHICLE_PRIVACIES)
    fun enablePrivacy(@Path(Parameters.VEHICLE_ID) vehicleId:String): Observable<Privacy>

    @PUT(Routes.VEHICLE_DISABLE_PRIVACY)
    fun disablePrivacy(@Path(Parameters.PRIVACY_ID) privacyId:String): Observable<Privacy>
}
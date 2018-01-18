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

import com.xee.sdk.fleet.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

/**
 * List of fleet endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface FleetsEndpoint {

    object Routes {
        const val FLEETS_MINE = "fleets/mine"
        const val FLEET_DRIVERS = "fleets/{${Parameters.FLEET_ID}}/drivers"
        const val FLEET_DRIVERS_ME = "fleets/{${Parameters.FLEET_ID}}/drivers/me"
        const val FLEET_VEHICLES = "fleets/{${Parameters.FLEET_ID}}/vehicles"
        const val FLEET_VEHICLE_LOANS = "fleets/{${Parameters.FLEET_ID}}/vehicles/{${Parameters.VEHICLE_ID}}/loans"
        const val FLEET_DRIVER_LOANS = "fleets/{${Parameters.FLEET_ID}}/drivers/{${Parameters.DRIVER_ID}}/loans"
        const val FLEET_END_LOAN = "fleets/{${Parameters.FLEET_ID}}/loans/{${Parameters.LOAN_ID}}"
        const val FLEET_STATUS = "fleets/{${Parameters.FLEET_ID}}/status"
        const val FLEET_TAGS = "fleets/{${Parameters.FLEET_ID}}/tags"
    }

    object Parameters {
        const val FLEET_ID = "fleetId"
        const val VEHICLE_ID = "vehicleId"
        const val DRIVER_ID = "driverId"
        const val LOAN_ID = "loanId"
    }

    @GET(Routes.FLEETS_MINE)
    fun getMyFleets(): Observable<List<Fleet>>

    @GET(Routes.FLEET_DRIVERS)
    fun getFleetDrivers(@Path(Parameters.FLEET_ID) fleetId: String): Observable<List<User>>

    @GET(Routes.FLEET_DRIVERS_ME)
    fun getDriver(@Path(Parameters.FLEET_ID) fleetId: String): Observable<User>

    @GET(Routes.FLEET_VEHICLES)
    fun getFleetVehicles(@Path(Parameters.FLEET_ID) fleetId: String): Observable<List<Vehicle>>

    @GET(Routes.FLEET_VEHICLE_LOANS)
    fun getVehicleLoans(@Path(Parameters.FLEET_ID) fleetId: String, @Path(Parameters.VEHICLE_ID) vehicleId: String, @QueryMap parameters: Map<String, @JvmSuppressWildcards Any>): Observable<List<Loan>>

    @GET(Routes.FLEET_DRIVER_LOANS)
    fun getDriverLoans(@Path(Parameters.FLEET_ID) fleetId: String, @Path(Parameters.DRIVER_ID) driverId: String, @QueryMap parameters: Map<String, @JvmSuppressWildcards Any>): Observable<List<Loan>>

    @POST(Routes.FLEET_VEHICLE_LOANS)
    fun startLoan(@Path(Parameters.FLEET_ID) fleetId: String, @Path(Parameters.VEHICLE_ID) vehicleId: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Observable<Loan> // todo: or Completable ???

    @PUT(Routes.FLEET_END_LOAN)
    fun endLoan(@Path(Parameters.FLEET_ID) fleetId: String, @Path(Parameters.LOAN_ID) loanId: String): Completable

    @GET(Routes.FLEET_STATUS)
    fun getStatus(@Path(Parameters.FLEET_ID) fleetId: String): Observable<FleetStatus>

    @GET(Routes.FLEET_TAGS)
    fun getTags(@Path(Parameters.FLEET_ID) fleetId: String): Observable<List<Tag>>

    @POST(Routes.FLEET_TAGS)
    fun createTag(@Path(Parameters.FLEET_ID) fleetId: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Observable<Tag>
}
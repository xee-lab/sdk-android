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

import com.xee.sdk.api.model.Location
import com.xee.sdk.api.model.Signal
import com.xee.sdk.api.model.Trip
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * List of [Trip] endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface TripsEndpoint {

    object Routes {
        const val TRIP = "trips/{${Parameters.TRIP_ID}}"
        const val TRIP_SIGNALS = "trips/{${Parameters.TRIP_ID}}/signals"
        const val TRIP_LOCATIONS = "trips/{${Parameters.TRIP_ID}}/locations"
    }

    object Parameters {
        const val TRIP_ID = "tripId"
    }

    @GET(Routes.TRIP)
    fun getTrip(@Path(Parameters.TRIP_ID) tripId:String): Observable<Trip>

    @GET(Routes.TRIP_SIGNALS)
    fun getTripSignals(@Path(Parameters.TRIP_ID) tripId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Signal>>

    @GET(Routes.TRIP_LOCATIONS)
    fun getTripLocations(@Path(Parameters.TRIP_ID) tripId:String, @QueryMap parameters:Map<String, @JvmSuppressWildcards Any>): Observable<List<Location>>
}
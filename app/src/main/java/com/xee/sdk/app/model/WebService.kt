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

package com.xee.sdk.app.model

data class WebService(val ws:WS = WS.NONE, val title: String = "", val isHeader: Boolean = false) {
    constructor(title:String = "", isHeader: Boolean = false) : this(WS.NONE, title, isHeader)
    constructor(ws:WS, title:String = "") : this(ws, title, false)
}

enum class WS {
    NONE,
    GET_USER,
    UPDATE_USER,
    GET_USER_VEHICLES,
    GET_AUTHORIZATIONS,
    REVOKE_AUTHORIZATION,

    GET_VEHICLE,
    GET_VEHICLE_DEVICE_DATA,
    GET_VEHICLE_PRIVACIES,
    GET_VEHICLE_TRIPS,
    GET_VEHICLE_SIGNALS,
    GET_VEHICLE_LOCATIONS,
    GET_VEHICLE_ACCELEROMETERS,
    GET_VEHICLE_STATUS,
    ASSOCIATE_VEHICLE,
    DISSOCIATE_VEHICLE,
    UPDATE_VEHICLE,
    ENABLE_PRIVACY,
    DISABLE_PRIVACY,

    GET_TRIP,
    GET_TRIP_SIGNALS,
    GET_TRIP_LOCATIONS,

    // FLEET
    GET_FLEETS_MINE,
    GET_FLEET_DRIVERS,
    GET_FLEET_DRIVERS_ME,
    GET_FLEET_VEHICLES,
    GET_FLEET_VEHICLE_LOANS,
    GET_FLEET_DRIVER_LOANS,
    START_LOAN,
    END_LOAN
}
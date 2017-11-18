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

import com.xee.sdk.api.model.Authorization
import com.xee.sdk.api.model.User
import com.xee.sdk.api.model.Vehicle
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

/**
 * List of [User] endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface UsersEndpoint {

    object Routes {
        const val ME = "users/${Parameters.ME}"
        const val USER = "users/{${Parameters.USER_ID}}"
        const val VEHICLES = "users/{${Parameters.USER_ID}}/vehicles"
        const val ASSOCIATE_VEHICLE = "users/{${Parameters.USER_ID}}/vehicles"
        const val AUTHORIZATIONS = "users/{${Parameters.USER_ID}}/authorizations"
        const val REVOKE_AUTHORIZATION = "authorizations/{${Parameters.AUTHORIZATION_ID}}"
    }

    object Parameters {
        const val ME = "me"
        const val USER_ID = "userId"
        const val AUTHORIZATION_ID = "authorizationId"
    }

    @GET(Routes.ME)
    fun getUser(): Observable<User>

    @GET(Routes.USER)
    fun getUser(@Path(Parameters.USER_ID) userId: String): Observable<User>

    @GET(Routes.VEHICLES)
    fun getUserVehicles(@Path(Parameters.USER_ID) userId: String): Observable<List<Vehicle>>

    @PATCH(Routes.USER)
    fun updateUser(@Path(Parameters.USER_ID) userId: String, @Body userToUpdate: User): Observable<User>

    @POST(Routes.ASSOCIATE_VEHICLE)
    fun associateVehicle(@Path(Parameters.USER_ID) userId: String, @Body body: Map<String, @JvmSuppressWildcards Any>): Observable<Vehicle>

    @GET(Routes.AUTHORIZATIONS)
    fun getAuthorizations(@Path(Parameters.USER_ID) userId: String) : Observable<List<Authorization>>

    @DELETE(Routes.REVOKE_AUTHORIZATION)
    fun revokeAuthorization(@Path(Parameters.AUTHORIZATION_ID) authorizationId:String): Completable
}
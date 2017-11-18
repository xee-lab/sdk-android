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

package com.xee.sdk.core.auth.endpoint

import com.xee.sdk.core.common.model.Token
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * List of OAuth endpoints
 * @author Julien Cholin
 * @since 4.0.0
 */
interface AuthEndpoint {

    object Routes {
        const val AUTHENTICATION = "oauth/authorize"
        const val ACCESS_TOKEN = "oauth/token"
        const val REGISTER = "oauth/register"
        const val REVOKE = "oauth/revoke"
    }

    object Parameters {
        const val GRANT_TYPE = "grant_type"
        const val CODE = "code"
        const val REFRESH_TOKEN = "refresh_token"
        const val CLIENT_ID = "client_id"
        const val REDIRECT_URL = "redirect_uri"
        const val AUTHORIZATION = "Authorization"
    }

    object GrantTypes {
        const val AUTHORIZATION_CODE = "authorization_code"
        const val REFRESH_TOKEN = "refresh_token"
    }

    @FormUrlEncoded
    @POST(Routes.ACCESS_TOKEN)
    fun obtainToken(@Field(Parameters.GRANT_TYPE) grantType: String, @Field(Parameters.CODE) authorizationCode: String, @Field(Parameters.REDIRECT_URL) redirectUri: String?, @Header(Parameters.AUTHORIZATION) basicOAuthHeader:String): Observable<Token>

    @FormUrlEncoded
    @POST(Routes.ACCESS_TOKEN)
    fun refreshTokenResponse(@Field(Parameters.GRANT_TYPE) grantType: String, @Field(Parameters.REFRESH_TOKEN) refreshToken: String): Observable<Response<Token>>

    @POST(Routes.REVOKE)
    fun revoke(@Body body: String, @Header(Parameters.AUTHORIZATION) bearerOAuthHeader:String): Completable
}
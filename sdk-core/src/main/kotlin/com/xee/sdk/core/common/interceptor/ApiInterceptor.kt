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

package com.xee.sdk.core.common.interceptor

import com.xee.sdk.core.auth.XeeAuth
import com.xee.sdk.core.auth.buildBasicAuthenticationHeader
import com.xee.sdk.core.auth.endpoint.AuthEndpoint
import com.xee.sdk.core.common.*
import com.xee.sdk.core.common.model.parseResponseError
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This interceptor refresh a token and relaunch the same request if the error is a token outdated error
 * @author Julien Cholin
 * @since 4.0.0
 */
class ApiInterceptor constructor(private val xeeEnv: XeeEnv, private val storage: TokenStorage) : Interceptor {

    private lateinit var authService: AuthEndpoint

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)
        val rawJson = originalResponse.body()!!.string()
        if (!originalResponse.isSuccessful) {
            val error = rawJson.parseResponseError()
            if ((originalResponse.code() == HttpCodes.UNAUTHORIZED_CODE) && (ApiMessages.ERROR_TOKEN_EXPIRED == error?.error || ApiMessages.ERROR_DESCRIPTION_TOKEN_EXPIRED == error?.errorDescription)) {
                val currentToken = storage.get()!!
                val refreshTokenResponse = authService.refreshTokenResponse(AuthEndpoint.GrantTypes.REFRESH_TOKEN, currentToken.refreshToken).blockingFirst()
                if (refreshTokenResponse.isSuccessful) {
                    storage.store(refreshTokenResponse.body()!!)
                    val requestBuilder = originalRequest.newBuilder().header(HttpHeaders.HEADER_AUTHORIZATION, HttpHeaders.HEADER_BEARER + refreshTokenResponse.body()!!.accessToken)
                    originalRequest = requestBuilder.build()
                    return chain.proceed(originalRequest)
                }
            }
        }

        return originalResponse.newBuilder().body(ResponseBody.create(originalResponse.body()!!.contentType(), rawJson)).build()
    }

    init {
        build()
    }

    private fun build() {
        val apiClientBuilder = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                            .header(HttpHeaders.HEADER_AUTHORIZATION, buildBasicAuthenticationHeader(xeeEnv))
                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(xeeEnv.connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(xeeEnv.readTimeout, TimeUnit.MILLISECONDS)

        // If the SDK is in debug mode, we log all the requests and the responses sent
        if (XeeAuth.enableOAuthLog) apiClientBuilder.addInterceptor(LogRequestInterceptor())

        // Build the retrofit interface and the API services
        val apiRetrofit = Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, XeeAuth.ROUTE_BASE, xeeEnv.environmentApi))
                .client(apiClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        authService = apiRetrofit.create<AuthEndpoint>(AuthEndpoint::class.java)
    }
}
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

package com.xee.sdk.core.auth

import android.util.Base64
import android.util.Log
import com.google.gson.*
import com.xee.sdk.core.common.HttpHeaders
import com.xee.sdk.core.common.XeeEnv
import com.xee.sdk.core.auth.endpoint.AuthEndpoint
import com.xee.sdk.core.common.interceptor.LogRequestInterceptor
import com.xee.sdk.core.common.model.buildThrowableError
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * XeeAuth class to call OAuth2 endpoints
 * @param environment the environment
 * @param enableLog true to enable logs, otherwise false
 * @author Julien Cholin
 * @since 4.0.0
 */
class XeeAuth @JvmOverloads constructor(environment: XeeEnv, private val enableLog: Boolean = false) {

    companion object {
        const val TAG = "Xee"
        const val ROUTE_BASE = "https://%s/"
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        private val CONVERTER_FACTORY: Converter.Factory = GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer()).create())
        private val LOG_INTERCEPTOR = LogRequestInterceptor()
        private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT, Locale.US)
        @JvmField
        var logged: Boolean = false
        var enableOAuthLog: Boolean = false
    }

    private var xeeEnv: XeeEnv = environment
    private lateinit var authEndpoint: AuthEndpoint

    init {
        enableOAuthLog = enableLog
        initAuthEndpoint(environment)
    }

    /**
     * Allows to serialize and deserialize a Date and format it
     */
    private class DateDeserializer : JsonSerializer<Date>, JsonDeserializer<Date> {
        @Synchronized override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
            return JsonPrimitive(DATE_FORMATTER.format(date))
        }

        @Synchronized
        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, arg1: Type, arg2: JsonDeserializationContext): Date? {
            val date = element.asString
            var formatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")

            try {
                return formatter.parse(date)
            } catch (e: ParseException) {
                try {
                    // if date parsed has failed with the default format, then try with the second one
                    formatter = SimpleDateFormat(DATE_FORMAT_WITH_MS, Locale.US)
                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                    return formatter.parse(date)
                } catch (e: ParseException) {
                    if (enableOAuthLog) Log.e(TAG, "Date deserialization error: ", e)
                }

                return null
            }
        }
    }

    /**
     * Init the Auth endpoint

     * @param env the [XeeEnv]
     */
    private fun initAuthEndpoint(env: XeeEnv) {
        val authClientBuilder = OkHttpClient.Builder()
                .connectTimeout(env.connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(env.readTimeout, TimeUnit.MILLISECONDS)

        // If the SDK is in debug mode, we log all the requests and the responses sent
        if (enableOAuthLog) authClientBuilder.addInterceptor(LOG_INTERCEPTOR)

        // Build the retrofit interface and the Auth service
        val authRetrofit = Retrofit.Builder()
                .baseUrl(String.format(Locale.FRANCE, ROUTE_BASE, env.environmentApi))
                .client(authClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CONVERTER_FACTORY)
                .build()

        authEndpoint = authRetrofit.create<AuthEndpoint>(AuthEndpoint::class.java)
    }

    /**
     * Authenticate the user
     * @param connectionCallback the [AuthenticationCallback]
     */
    fun connect(connectionCallback: AuthenticationCallback) {
        val currentToken = xeeEnv.tokenStorage?.get()
        // Create a potential callback if we need to obtain of refresh the token
        if (currentToken == null) {
            AuthenticationActivity.callback = object : AuthenticationActivity.CodeCallback {
                override fun onError(error: Throwable) {
                    connectionCallback.onError(error)
                }

                override fun onSuccess(code: String, redirectUri: String?) {
                    authEndpoint.obtainToken(AuthEndpoint.GrantTypes.AUTHORIZATION_CODE, code, redirectUri, buildBasicAuthenticationHeader(xeeEnv))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ token ->
                                xeeEnv.tokenStorage?.store(token)
                                logged = true
                                connectionCallback.onSuccess()
                            }, { error ->
                                connectionCallback.onError(error)
                            })
                }
            }
            xeeEnv.context.startActivity(AuthenticationActivity.intent(xeeEnv.context, xeeEnv.oAuthClient, xeeEnv.environmentApi))
        } else {
            logged = true
            connectionCallback.onSuccess()
        }
    }

    /**
     * Register a user
     * @param registrationCallback the [RegistrationCallback]
     */
    fun register(registrationCallback: RegistrationCallback) {
        RegistrationActivity.callback = object : RegistrationActivity.CodeCallback {
            override fun onCanceled() {
                registrationCallback.onCanceled()
            }

            override fun onError(error: Throwable) {
                registrationCallback.onError(error)
            }

            override fun onRegistered() {
                registrationCallback.onRegistered()
            }

            override fun onLoggedAfterRegistration(code: String, redirectUri: String?) {
                authEndpoint.obtainToken(AuthEndpoint.GrantTypes.AUTHORIZATION_CODE, code, redirectUri, buildBasicAuthenticationHeader(xeeEnv))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ token ->
                            xeeEnv.tokenStorage?.store(token)
                            logged = true
                            registrationCallback.onLoggedAfterRegistration()
                        }, { error ->
                            registrationCallback.onError(error)
                        })
            }
        }
        xeeEnv.context.startActivity(RegistrationActivity.intent(xeeEnv.context, xeeEnv.oAuthClient, xeeEnv.environmentApi))
    }

    /**
     * Disconnect the user by clearing the associated token
     * @param disconnectCallback the [DisconnectCallback]
     */
    fun disconnect(disconnectCallback: DisconnectCallback) {
        if (logged) {
            handleCompletableError(authEndpoint.revoke("", buildBearerAuthenticationHeader(xeeEnv)))
                    .composeSubAndObs()
                    .doFinally {
                        xeeEnv.tokenStorage?.dump()
                        logged = false
                        disconnectCallback.onCompleted()
                    }
                    .subscribe({
                        if (enableOAuthLog) Log.i(TAG, "token was successful revoked")
                    }) { error ->
                        if (enableOAuthLog) Log.e(TAG, "token couldn't be revoked", error)
                    }
        } else {
            xeeEnv.tokenStorage?.dump()
            logged = false
            disconnectCallback.onCompleted()
        }
    }

    /**
     * @return if the user is logged
     */
    fun isLogged(): Boolean = logged

    //region [ERROR]
    /**
     * Handle error from an [Completable]
     */
    private fun handleCompletableError(o: Completable?): Completable {
        return o!!.onErrorResumeNext { t: Throwable ->
            Completable.error(t.buildThrowableError())
        }
    }
    //endregion
}

fun Completable.composeSubAndObs(): Completable = compose({
    it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
})

private fun buildBearerAuthenticationHeader(xeeEnv: XeeEnv): String =
        HttpHeaders.HEADER_BEARER + xeeEnv.tokenStorage?.get()?.accessToken

fun buildBasicAuthenticationHeader(xeeEnv: XeeEnv): String {
    return HttpHeaders.HEADER_BASIC + Base64.encodeToString((xeeEnv.oAuthClient.clientId + ":" +
            xeeEnv.oAuthClient.clientSecret).toByteArray(), Base64.NO_WRAP)
}
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

import android.util.Log
import okhttp3.Interceptor
import okhttp3.ResponseBody
import java.io.IOException

/**
 * This interceptor logs all requests when debug mode is enabled
 * @author Julien Cholin
 * @since 4.0.0
 */
class LogRequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        val response = chain.proceed(request)
        val rawJson = response.body()!!.string()

        Log.v(TAG, "==============================================================================================================================")
        Log.v(TAG, "URL: " + request.url().toString())
        Log.v(TAG, "RESPONSE: " + rawJson)
        Log.v(TAG, "CODE: " + response.code())
        Log.v(TAG, "HEADERS: " + request.headers())
        Log.v(TAG, "==============================================================================================================================")

        // Recreate the item before returning it because body can be read only once
        return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), rawJson)).build()
    }

    companion object {
        private val TAG = "XeeSdkV4"
    }
}
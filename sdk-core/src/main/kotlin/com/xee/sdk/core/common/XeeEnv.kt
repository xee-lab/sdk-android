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

package com.xee.sdk.core.common

import android.content.Context
import com.xee.sdk.core.auth.OAuth2Client

/**
 * XeeEnv to set an environment linked to the [OAuth2Client]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class XeeEnv(val context: Context, val oAuthClient: OAuth2Client, val connectTimeout: Long, val readTimeout: Long, val environment: String, val environmentApi: String) {

    var tokenStorage: TokenStorage? = TokenStorage[context]

    companion object {
        const val ENVIRONMENT: String = "api"
        const val ENVIRONMENTAPI: String = "api"
        const val TIMEOUT: Long = 30 * 1000
        lateinit var instance: TokenStorage
            private set
    }

    constructor(context: Context, oAuthClient: OAuth2Client) :
            this(context = context, oAuthClient = oAuthClient, connectTimeout = TIMEOUT, readTimeout = TIMEOUT, environment = ENVIRONMENT, environmentApi = ENVIRONMENTAPI)

    constructor(context: Context, oAuthClient: OAuth2Client, environment: String, environmentApi: String) :
            this(context = context, oAuthClient = oAuthClient, connectTimeout = TIMEOUT, readTimeout = TIMEOUT, environment = environment, environmentApi = environmentApi)

    constructor(context: Context, oAuthClient: OAuth2Client, timeout: Long) :
            this(context = context, oAuthClient = oAuthClient, connectTimeout = timeout, readTimeout = timeout, environment = ENVIRONMENT, environmentApi = ENVIRONMENTAPI)

    constructor(context: Context, oAuthClient: OAuth2Client, connectTimeout: Long, readTimeout: Long) :
            this(context = context, oAuthClient = oAuthClient, connectTimeout = connectTimeout, readTimeout = readTimeout, environment = ENVIRONMENT, environmentApi = ENVIRONMENTAPI)
}
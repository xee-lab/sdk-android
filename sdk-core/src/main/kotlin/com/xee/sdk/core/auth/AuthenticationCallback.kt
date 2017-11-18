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

/**
 * Authentication callback when user authenticates with OAuth2
 * @author Julien Cholin
 * @since 4.0.0
 */
interface AuthenticationCallback {
    /**
     * Fired when authentication has failed
     * @param error the [Throwable]
     */
    fun onError(error: Throwable)

    /**
     * Fired when user has been authenticated successful
     */
    fun onSuccess()
}
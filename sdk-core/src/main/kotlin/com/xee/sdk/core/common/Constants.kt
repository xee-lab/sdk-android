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

/**
 * Constants
 * @author Julien Cholin
 * @since 4.0.0
 */

/**
 * List of HTTP codes
 */
object HttpCodes {
    const val UNAUTHORIZED_CODE = 401
    const val FORBIDDEN_CODE = 403
    const val BAD_GATEWAY = 502
    const val GATEWAY_TIMEOUT = 504
}

/**
 * List of HTTP headers
 */
object HttpHeaders {
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_BEARER = "Bearer "
    const val HEADER_BASIC = "Basic "
}

/**
 * Payload of our [com.xee.sdk.core.common.model.Error]
 */
object ApiMessages {
    const val ERROR_TOKEN_EXPIRED = "token_expired"
    const val ERROR_DESCRIPTION_TOKEN_EXPIRED = "Token has expired"
}
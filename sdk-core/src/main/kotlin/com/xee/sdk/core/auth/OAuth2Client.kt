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

import android.os.Parcel
import android.os.Parcelable

/**
 * OAuth2Client to config the OAuth client
 * @param clientId the client id of your application
 * @param clientSecret the client secret of your application
 * @param redirectUri optional, the uri our server will redirect to once the user has entered its credentials
 * @param scopes the scopes to use
 */
data class OAuth2Client(val clientId: String?,
                        val clientSecret: String?,
                        val redirectUri: String? = "",
                        val scopes: List<String>) : Parcelable {

    private constructor (builder: Builder) : this(builder.clientId, builder.clientSecret, builder.redirectUri, builder.scopes)

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(clientId)
        writeString(clientSecret)
        writeString(redirectUri)
        writeStringList(scopes)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OAuth2Client> = object : Parcelable.Creator<OAuth2Client> {
            override fun createFromParcel(source: Parcel): OAuth2Client = OAuth2Client(source)
            override fun newArray(size: Int): Array<OAuth2Client?> = arrayOfNulls(size)
        }
    }

    /**
     * Builder for creating [OAuth2Client]
     */
    class Builder() : Parcelable {
        internal
        var clientId: String? = ""
            private set

        internal
        var clientSecret: String? = ""
            private set

        internal
        var redirectUri: String? = ""
            private set

        internal
        var scopes: List<String> = listOf()
            private set

        constructor(source: Parcel) : this(
        )

        fun clientId(clientId: String?) = apply { this.clientId = clientId }
        fun clientSecret(clientSecret: String?) = apply { this.clientSecret = clientSecret }
        fun redirectUri(redirectUri: String?) = apply { this.redirectUri = redirectUri }
        fun scopes(scopes: List<String>) = apply { this.scopes = scopes }
        fun build() = OAuth2Client(this)

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Builder> = object : Parcelable.Creator<Builder> {
                override fun createFromParcel(source: Parcel): Builder = Builder(source)
                override fun newArray(size: Int): Array<Builder?> = arrayOfNulls(size)
            }
        }
    }
}
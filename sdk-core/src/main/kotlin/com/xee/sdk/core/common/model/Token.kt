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

package com.xee.sdk.core.common.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Token which is the unique and returned when user is authenticated from our API
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Token(@SerializedName("access_token") var accessToken: String,
                 @SerializedName("refresh_token") var refreshToken: String,
                 @SerializedName("expires_in") var expiresIn: Long,
                 @SerializedName("scope") var scope: String,
                 @SerializedName("token_type") var tokenType: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(accessToken)
        writeString(refreshToken)
        writeLong(expiresIn)
        writeString(scope)
        writeString(tokenType)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Token> = object : Parcelable.Creator<Token> {
            override fun createFromParcel(source: Parcel): Token = Token(source)
            override fun newArray(size: Int): Array<Token?> = arrayOfNulls(size)
        }
    }
}
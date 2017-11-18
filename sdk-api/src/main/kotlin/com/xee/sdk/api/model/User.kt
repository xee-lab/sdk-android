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

package com.xee.sdk.api.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * User
 * @author Julien Cholin
 * @since 4.0.0
 */
data class User(@SerializedName("id") var id: String,
                @SerializedName("firstName") var firstName: String?,
                @SerializedName("lastName") var lastName: String?,
                @SerializedName("email") var email: String?,
                @SerializedName("gender") var gender: Gender?,
                @SerializedName("createdAt") var createdAt: Date?,
                @SerializedName("updatedAt") var updatedAt: Date?) : Parcelable {

    enum class Gender {
        MALE, FEMALE
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader)?.let { Gender.values()[it as Int] },
            source.readSerializable() as Date?,
            source.readSerializable() as Date?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(firstName)
        writeString(lastName)
        writeString(email)
        writeValue(gender?.ordinal)
        writeSerializable(createdAt)
        writeSerializable(updatedAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
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

package com.xee.sdk.fleet.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Fleet
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Fleet(@SerializedName("id") var id: String,
                 @SerializedName("name") var name: String?,
                 @SerializedName("company") var company: String?,
                 @SerializedName("active") var active: Boolean,
                 @SerializedName("joinedAt") var joinedAt: Date?,
                 @SerializedName("role") var role: User.Role?) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            1 == source.readInt(),
            source.readSerializable() as Date?,
            source.readValue(Int::class.java.classLoader)?.let { User.Role.values()[it as Int] }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(company)
        writeInt((if (active) 1 else 0))
        writeSerializable(joinedAt)
        writeValue(role?.ordinal)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Fleet> = object : Parcelable.Creator<Fleet> {
            override fun createFromParcel(source: Parcel): Fleet = Fleet(source)
            override fun newArray(size: Int): Array<Fleet?> = arrayOfNulls(size)
        }
    }
}
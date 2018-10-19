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
 * Location
 * @author Oscar Perez
 * @since 4.1.20
 */
data class TripLocation(@SerializedName("street") val street: String? = null,
                        @SerializedName("city") val city: String? = null,
                        @SerializedName("country") val country: String? = null,
                        @SerializedName("latitude") val latitude: Double? = null,
                        @SerializedName("longitude") val longitude: Double? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(street)
        writeString(city)
        writeString(country)
        writeValue(latitude)
        writeValue(longitude)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TripLocation> = object : Parcelable.Creator<TripLocation> {
            override fun createFromParcel(source: Parcel): TripLocation = TripLocation(source)
            override fun newArray(size: Int): Array<TripLocation?> = arrayOfNulls(size)
        }
    }
}
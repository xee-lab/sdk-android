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
 * Trip of the [Vehicle]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Trip @JvmOverloads constructor(@SerializedName("id") var id: String,
                                          @SerializedName("stats") var stats: Stats? = null,
                                          @SerializedName("startLocation") var startLocation: Location? = null,
                                          @SerializedName("endLocation") var endLocation: Location? = null,
                                          @SerializedName("vehicleId") var vehicleId: String? = null,
                                          @SerializedName("createdAt") var createdAt: Date? = null,
                                          @SerializedName("updatedAt") var updatedAt: Date? = null,
                                          @SerializedName("behaviors") var behaviors: List<Behavior>? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelable<Stats>(Stats::class.java.classLoader),
            source.readParcelable<Location>(Location::class.java.classLoader),
            source.readParcelable<Location>(Location::class.java.classLoader),
            source.readString(),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            source.createTypedArrayList(Behavior.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeParcelable(stats, 0)
        writeParcelable(startLocation, 0)
        writeParcelable(endLocation, 0)
        writeString(vehicleId)
        writeSerializable(createdAt)
        writeSerializable(updatedAt)
        writeTypedList(behaviors)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Trip> = object : Parcelable.Creator<Trip> {
            override fun createFromParcel(source: Parcel): Trip = Trip(source)
            override fun newArray(size: Int): Array<Trip?> = arrayOfNulls(size)
        }
    }
}
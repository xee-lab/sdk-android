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
data class Trip @JvmOverloads constructor (
        @SerializedName("id") val id: String,
        @SerializedName("vehicleId") val vehicleId: String? = null,
        @SerializedName("startDate") val startDate: Date? = null,
        @SerializedName("endDate") val endDate: Date? = null,
        @SerializedName("humanUsedTime") val humanUsedTime: String? = null,
        @SerializedName("duration") val duration: Long? = null, // in seconds
        @SerializedName("distance") val distance: Double? = null, // in KM
        @SerializedName("score") private var score: Double? = null,
        @SerializedName("startLocation") val startLocation: TripLocation? = null,
        @SerializedName("endLocation") val endLocation: TripLocation? = null,
        @SerializedName("locations") private var locations: List<Location>? = null,
        @SerializedName("behaviors") val behaviors: List<Behavior>? = null,
        @SerializedName("type") val type: Int? = 0,
        @SerializedName("matter") val matter: String? = "",
        @SerializedName("customer") val customer: String? = "",
        @SerializedName("contactPerson") val contactPerson: String? = "",
        @SerializedName("additionalInformation") val additionalInformation: String? = ""
        ) : Parcelable {

    fun getLocations(): List<Location>? {
        return locations
    }

    fun setLocations(locations: List<Location>?) {
        this.locations = locations
    }

    fun getScore(): Double? {
        return score
    }

    fun setScore(score: Double?) {
        this.score = score
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            source.readString(),
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readParcelable<TripLocation>(TripLocation::class.java.classLoader),
            source.readParcelable<TripLocation>(TripLocation::class.java.classLoader),
            source.createTypedArrayList(Location.CREATOR),
            source.createTypedArrayList(Behavior.CREATOR),
            source.readValue(Long::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(vehicleId)
        writeSerializable(startDate)
        writeSerializable(endDate)
        writeString(humanUsedTime)
        writeValue(duration)
        writeValue(distance)
        writeValue(score)
        writeParcelable(startLocation, 0)
        writeParcelable(endLocation, 0)
        writeTypedList(locations)
        writeTypedList(behaviors)
        writeValue(type)
        writeString(matter)
        writeString(customer)
        writeString(contactPerson)
        writeString(additionalInformation)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Trip> = object : Parcelable.Creator<Trip> {
            override fun createFromParcel(source: Parcel): Trip = Trip(source)
            override fun newArray(size: Int): Array<Trip?> = arrayOfNulls(size)
        }
    }
}
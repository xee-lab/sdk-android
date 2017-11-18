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
 * Trip of the [Vehicle]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Trip(@SerializedName("id") val id: String,
                @SerializedName("mileage") val mileage: Mileage?,
                @SerializedName("usedTime") val usedTime: UsedTime?,
                @SerializedName("startLocation") val startLocation: Location?,
                @SerializedName("endLocation") val endLocation: Location?,
                @SerializedName("vehicleId") val vehicleId: String?,
                @SerializedName("createdAt") val createdAt: Date?,
                @SerializedName("updateAt") val updateAt: Date?) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelable<Mileage>(Mileage::class.java.classLoader),
            source.readParcelable<UsedTime>(UsedTime::class.java.classLoader),
            source.readParcelable<Location>(Location::class.java.classLoader),
            source.readParcelable<Location>(Location::class.java.classLoader),
            source.readString(),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeParcelable(mileage, 0)
        writeParcelable(usedTime, 0)
        writeParcelable(startLocation, 0)
        writeParcelable(endLocation, 0)
        writeString(vehicleId)
        writeSerializable(createdAt)
        writeSerializable(updateAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Trip> = object : Parcelable.Creator<Trip> {
            override fun createFromParcel(source: Parcel): Trip = Trip(source)
            override fun newArray(size: Int): Array<Trip?> = arrayOfNulls(size)
        }
    }
}

/**
 * Mileage of the [Trip]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Mileage(@SerializedName("type") val type: String,
                   @SerializedName("value") val value: Double,
                   @SerializedName("unit") val unit: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(type)
        writeDouble(value)
        writeString(unit)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Mileage> = object : Parcelable.Creator<Mileage> {
            override fun createFromParcel(source: Parcel): Mileage = Mileage(source)
            override fun newArray(size: Int): Array<Mileage?> = arrayOfNulls(size)
        }
    }
}

/**
 * UsedTime of the [Trip]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class UsedTime(@SerializedName("type") val type: String,
                    @SerializedName("value") val value: Double,
                    @SerializedName("unit") val unit: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(type)
        writeDouble(value)
        writeString(unit)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UsedTime> = object : Parcelable.Creator<UsedTime> {
            override fun createFromParcel(source: Parcel): UsedTime = UsedTime(source)
            override fun newArray(size: Int): Array<UsedTime?> = arrayOfNulls(size)
        }
    }
}
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
 * Status of the [Vehicle]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Status @JvmOverloads constructor(@SerializedName("vehicleId") var vehicleId: String? = null,
                                            @SerializedName("location") var location: Location? = null,
                                            @SerializedName("accelerometer") var accelerometer: Accelerometer? = null,
                                            @SerializedName("createdAt") var createdAt: Date? = null,
                                            @SerializedName("updatedAt") var updatedAt: Date? = null,
                                            @SerializedName("signals") var signals: List<Signal>? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelable<Location?>(Location::class.java.classLoader),
            source.readParcelable<Accelerometer?>(Accelerometer::class.java.classLoader),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            source.createTypedArrayList(Signal.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(vehicleId)
        writeParcelable(location, 0)
        writeParcelable(accelerometer, 0)
        writeSerializable(createdAt)
        writeSerializable(updatedAt)
        writeTypedList(signals)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Status> = object : Parcelable.Creator<Status> {
            override fun createFromParcel(source: Parcel): Status = Status(source)
            override fun newArray(size: Int): Array<Status?> = arrayOfNulls(size)
        }
    }
}
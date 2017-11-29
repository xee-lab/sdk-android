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
 * Accelerometer of the XeeCONNECT
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Accelerometer @JvmOverloads constructor(@SerializedName("x") var x: Double = 0.0,
                                                   @SerializedName("y") var y: Double = 0.0,
                                                   @SerializedName("z") var z: Double = 0.0,
                                                   @SerializedName("date") var date: Date? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readSerializable() as Date?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(x)
        writeDouble(y)
        writeDouble(z)
        writeSerializable(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Accelerometer> = object : Parcelable.Creator<Accelerometer> {
            override fun createFromParcel(source: Parcel): Accelerometer = Accelerometer(source)
            override fun newArray(size: Int): Array<Accelerometer?> = arrayOfNulls(size)
        }
    }
}
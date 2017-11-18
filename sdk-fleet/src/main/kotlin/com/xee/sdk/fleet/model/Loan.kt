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
 * Loan of a [Vehicle]
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Loan(@SerializedName("id") var id: String,
                @SerializedName("startedAt") var startedAt: Date?,
                @SerializedName("endedAt") var endedAt: Date?,
                @SerializedName("vehicle") var vehicle: Vehicle?,
                @SerializedName("driver") var driver: User?) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            source.readParcelable<Vehicle>(Vehicle::class.java.classLoader),
            source.readParcelable<User>(User::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeSerializable(startedAt)
        writeSerializable(endedAt)
        writeParcelable(vehicle, 0)
        writeParcelable(driver, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Loan> = object : Parcelable.Creator<Loan> {
            override fun createFromParcel(source: Parcel): Loan = Loan(source)
            override fun newArray(size: Int): Array<Loan?> = arrayOfNulls(size)
        }
    }
}
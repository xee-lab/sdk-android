/*
 * Copyright 2018 Xee
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
 *
 * @author Julien Cholin
 */

package com.xee.sdk.api.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Signal
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Stats @JvmOverloads constructor(@SerializedName("distance") var distance: Double?,
                                           @SerializedName("duration") var date: Long? = null) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Long::class.java.classLoader) as Long?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(distance)
        writeValue(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Stats> = object : Parcelable.Creator<Stats> {
            override fun createFromParcel(source: Parcel): Stats = Stats(source)
            override fun newArray(size: Int): Array<Stats?> = arrayOfNulls(size)
        }
    }
}
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

/**
 * Tag
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Tag @JvmOverloads constructor(@SerializedName("id") var id: String = "",
                                         @SerializedName("name") var name: String? = null,
                                         @SerializedName("color") var color: String? = null,
                                         @SerializedName("icon") var icon: String? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(color)
        writeString(icon)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Tag> = object : Parcelable.Creator<Tag> {
            override fun createFromParcel(source: Parcel): Tag = Tag(source)
            override fun newArray(size: Int): Array<Tag?> = arrayOfNulls(size)
        }
    }
}
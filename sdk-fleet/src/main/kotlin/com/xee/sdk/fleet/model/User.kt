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
 * User
 * @author Julien Cholin
 * @since 4.0.0
 */
data class User @JvmOverloads constructor(@SerializedName("id") var id: String,
                                          @SerializedName("firstName") var firstName: String,
                                          @SerializedName("lastName") var lastName: String,
                                          @SerializedName("gender") var gender: Gender = Gender.UNKNOWN,
                                          @SerializedName("role") var role: Role? = null,
                                          @SerializedName("loan") var loan: Loan? = null,
                                          @SerializedName("tags") var tags: List<Tag>? = null,
                                          @SerializedName("nextChecking") var nextChecking: Date? = null,
                                          @SerializedName("licenseValidityDate") var licenseValidityDate: Date? = null,
                                          @SerializedName("nextCheckingNotification") var nextCheckingNotification: Boolean = false,
                                          @SerializedName("licenseValidityDateNotification") var licenseValidityDateNotification: Boolean = false,
                                          @SerializedName("tripsReminder") var tripsReminder: Boolean = false,
                                          @SerializedName( "leftAt") var leftAt: String? = null) : Parcelable {
    enum class Role {
        OWNER, SUPERVISOR, DRIVER, SUPPORT
    }

    enum class Gender {
        MALE, FEMALE, UNKNOWN
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            Gender.values()[source.readInt()],
            source.readValue(Int::class.java.classLoader)?.let { Role.values()[it as Int] },
            source.readParcelable<Loan>(Loan::class.java.classLoader),
            source.createTypedArrayList(Tag.CREATOR),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            1 == source.readInt(),
            1 == source.readInt(),
            1 == source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(firstName)
        writeString(lastName)
        writeValue(gender?.ordinal)
        writeValue(role?.ordinal)
        writeParcelable(loan, 0)
        writeTypedList(tags)
        writeSerializable(nextChecking)
        writeSerializable(licenseValidityDate)
        writeInt((if (nextCheckingNotification) 1 else 0))
        writeInt((if (licenseValidityDateNotification) 1 else 0))
        writeInt((if (tripsReminder) 1 else 0))
        writeString(leftAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
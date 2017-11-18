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
 * Authorization of the [Client] fired by the [User] once authenticated
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Authorization(@SerializedName("id") val id: String,
                         @SerializedName("userId") val userId: String,
                         @SerializedName("client") val client: Client,
                         @SerializedName("scopes") val scopes: List<Scope>,
                         @SerializedName("createdAt") val createdAt: Date) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readParcelable<Client>(Client::class.java.classLoader),
            source.createTypedArrayList(Scope.CREATOR),
            source.readSerializable() as Date
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(userId)
        writeParcelable(client, 0)
        writeTypedList(scopes)
        writeSerializable(createdAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Authorization> = object : Parcelable.Creator<Authorization> {
            override fun createFromParcel(source: Parcel): Authorization = Authorization(source)
            override fun newArray(size: Int): Array<Authorization?> = arrayOfNulls(size)
        }
    }
}

/**
 * Client
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Client(@SerializedName("id") val id: String,
                  @SerializedName("name") val name: String,
                  @SerializedName("description") val description: String?,
                  @SerializedName("icon") val icon: String?) : Parcelable {
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
        writeString(description)
        writeString(icon)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Client> = object : Parcelable.Creator<Client> {
            override fun createFromParcel(source: Parcel): Client = Client(source)
            override fun newArray(size: Int): Array<Client?> = arrayOfNulls(size)
        }
    }
}

/**
 * Scope
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Scope(@SerializedName("id") val id: String,
                 @SerializedName("identifier") val identifier: String,
                 @SerializedName("visibility") val visibility: Visibility) : Parcelable {
    enum class Visibility {
        PROTECTED, PRIVATE, PUBLIC
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            Visibility.values()[source.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(identifier)
        writeInt(visibility.ordinal)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Scope> = object : Parcelable.Creator<Scope> {
            override fun createFromParcel(source: Parcel): Scope = Scope(source)
            override fun newArray(size: Int): Array<Scope?> = arrayOfNulls(size)
        }
    }
}
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

package com.xee.sdk.core.common.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

/**
 * Error corresponding to the API error payload
 * @author Julien Cholin
 * @since 4.0.0
 */
data class Error(@SerializedName("error") var error: String?,
                 @SerializedName("error_description") var errorDescription: String?,
                 @SerializedName("error_details") var errorDetails: List<ErrorDetails>?,
                 @SerializedName("code") var code: Int?) : Throwable(), Parcelable {

    constructor(error:String, errorDescription: String) : this(error, errorDescription, null, null)

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.createTypedArrayList(ErrorDetails.CREATOR),
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(error)
        writeString(errorDescription)
        writeTypedList(errorDetails)
        writeValue(code)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Error> = object : Parcelable.Creator<Error> {
            override fun createFromParcel(source: Parcel): Error = Error(source)
            override fun newArray(size: Int): Array<Error?> = arrayOfNulls(size)
        }
    }
}

/**
 * Error details corresponding to the API error payload
 * @author Julien Cholin
 * @since 4.0.0
 */
data class ErrorDetails(@SerializedName("field") var field: String?,
                        @SerializedName("constraint") var constraint: String?,
                        @SerializedName("description") var description: String?) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(field)
        writeString(constraint)
        writeString(description)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ErrorDetails> = object : Parcelable.Creator<ErrorDetails> {
            override fun createFromParcel(source: Parcel): ErrorDetails = ErrorDetails(source)
            override fun newArray(size: Int): Array<ErrorDetails?> = arrayOfNulls(size)
        }
    }
}

/**
 * Parse error response from API to create an [Error]
 */
fun String.parseResponseError(): Error? = when {
    this.startsWith("{") ->
        Gson().fromJson<Error>(this, Error::class.java)
    this.startsWith("[") -> {
        val listErrorType = object : TypeToken<List<Error>>() {}.type
        Gson().fromJson<List<Error>>(this, listErrorType)[0]
    }
    else -> Error("unexpected_error", this)
}

/**
 * Build either an [Error] or [Throwable]
 * @return either an [Error] or a [Throwable]
 */
fun Throwable.buildThrowableError(): Throwable {
    val error: Error?
    if (this is HttpException) {
        val errorBody: String = this.response().errorBody()!!.string()
        when {
            errorBody.startsWith("{") -> {
                error = Gson().fromJson<Error>(errorBody, Error::class.java)
                error?.code = this.response().code()
            }
            errorBody.startsWith("[") -> {
                val listErrorType = object : TypeToken<List<Error>>() {}.type
                error = Gson().fromJson<List<Error>>(errorBody, listErrorType)[0]
                error.code = this.response().code()
            }
            else -> error = Error(errorBody, this.response().message(), null, this.response().code())
        }
    } else {
        return this
    }
    return error!!
}
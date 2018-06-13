package com.xee.sdk.fleet.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

/**
 * Behavior of the [Trip]
 * @author Oscar Rezoloco
 * @since 4.0.0
 */
data class Behavior @JvmOverloads constructor(@SerializedName("id") var id: String,
                                          @SerializedName("type") var type: String? = null,
                                          @SerializedName("value") var value: Double = 0.0,
                                          @SerializedName("startDate") var startDate: Date? = null,
                                          @SerializedName("endDate") var endDate: Date? = null,
                                          @SerializedName("defaultValue") var defaultValue: Double = 0.0,
                                              @SerializedName("count") var count: Int = 0) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readSerializable() as Date?,
            source.readSerializable() as Date?,
            source.readDouble(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(type)
        writeDouble(value)
        writeSerializable(startDate)
        writeSerializable(endDate)
        writeDouble(defaultValue)
        writeInt(count)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Behavior> = object : Parcelable.Creator<Behavior> {
            override fun createFromParcel(source: Parcel): Behavior = Behavior(source)
            override fun newArray(size: Int): Array<Behavior?> = arrayOfNulls(size)
        }
    }
}

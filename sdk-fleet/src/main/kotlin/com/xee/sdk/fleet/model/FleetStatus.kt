package com.xee.sdk.fleet.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FleetStatus @JvmOverloads constructor(@SerializedName("id") var id: String,
                                                 @SerializedName("name") var name: String? = null,
                                                 @SerializedName("company") var company: String? = null,
                                                 @SerializedName("vehicles") var vehicles: List<Status>? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.createTypedArrayList(Status.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(company)
        writeTypedList(vehicles)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FleetStatus> = object : Parcelable.Creator<FleetStatus> {
            override fun createFromParcel(source: Parcel): FleetStatus = FleetStatus(source)
            override fun newArray(size: Int): Array<FleetStatus?> = arrayOfNulls(size)
        }
    }
}
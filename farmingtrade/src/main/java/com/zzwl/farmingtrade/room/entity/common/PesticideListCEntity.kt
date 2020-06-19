package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.zzwl.farmingtrade.room.entity.typeConverters.PesticideListConvertes

@Entity
@TypeConverters(PesticideListConvertes::class)
data class PesticideListCEntity(
        @PrimaryKey
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("children") var children: List<ChildrenP1> = listOf()
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            ArrayList<ChildrenP1>().apply { source.readList(this, ChildrenP1::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeList(children)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PesticideListCEntity> = object : Parcelable.Creator<PesticideListCEntity> {
            override fun createFromParcel(source: Parcel): PesticideListCEntity = PesticideListCEntity(source)
            override fun newArray(size: Int): Array<PesticideListCEntity?> = arrayOfNulls(size)
        }
    }
}

data class ChildrenP1(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("children") var children: List<ChildrenP2> = listOf()
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            ArrayList<ChildrenP2>().apply { source.readList(this, ChildrenP2::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeList(children)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ChildrenP1> = object : Parcelable.Creator<ChildrenP1> {
            override fun createFromParcel(source: Parcel): ChildrenP1 = ChildrenP1(source)
            override fun newArray(size: Int): Array<ChildrenP1?> = arrayOfNulls(size)
        }
    }
}

data class ChildrenP2(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ChildrenP2> = object : Parcelable.Creator<ChildrenP2> {
            override fun createFromParcel(source: Parcel): ChildrenP2 = ChildrenP2(source)
            override fun newArray(size: Int): Array<ChildrenP2?> = arrayOfNulls(size)
        }
    }
}
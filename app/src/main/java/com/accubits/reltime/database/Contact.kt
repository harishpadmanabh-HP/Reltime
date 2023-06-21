package com.accubits.reltime.database

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "contact_table")
data class Contact(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "contact_name")
    var contactName: String,

    @ColumnInfo(name = "contact_number")
    var contactNumber: String,

    @ColumnInfo(name = "contact_type")
    var contactType: String,

    @ColumnInfo(name = "user_id")
    var userId: String? = null,

    @ColumnInfo(name = "contact_image_uri")
    var contactImageUri: String?,

    @ColumnInfo(name = "exist")
    var exist: Boolean = false

) : Parcelable
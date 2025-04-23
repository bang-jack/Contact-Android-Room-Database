package com.example.mycrud.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")  // <--- ini penting!
    val id: Int? = null,

    val fullname: String,
    val email: String,
    val phone: String,
    val address: String,
    val status: String
)

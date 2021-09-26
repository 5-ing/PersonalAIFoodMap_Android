package com.example.personalaifoodmap.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.reflect.Constructor

@Entity(tableName = "user_photo_table")
data class UserPhoto(
    @PrimaryKey
    var uri: String,
    var isFood: Boolean,
    var foodName: String,
    var lat: Float,
    var lon: Float
)
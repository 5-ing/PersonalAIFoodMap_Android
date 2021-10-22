package com.example.personalaifoodmap.data

import androidx.room.ColumnInfo
import java.io.Serializable

data class FPlace(
    var resName: String,
    var resAddress: String,
    var resCategory: String,
    var resURL: String
) : Serializable
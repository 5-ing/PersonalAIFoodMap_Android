package com.example.personalaifoodmap.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable
import java.lang.reflect.Constructor

@Entity(tableName = "user_photo_table")
data class UserPhoto(
    @PrimaryKey
    var uri: String,
    var isFood: Boolean,
    var foodName: String,
    var lat: Double,
    var lon: Double,
    @Embedded var fPlace: FPlace
) : Serializable, ClusterItem{
    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }
}
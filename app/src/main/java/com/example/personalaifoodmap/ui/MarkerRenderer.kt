package com.example.personalaifoodmap.ui

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.UserPhoto
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerRenderer(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<UserPhoto>
) :
    DefaultClusterRenderer<UserPhoto>(context, map, clusterManager) {

//    val markerView: View = LayoutInflater.from(FoodMapApplication.applicationContext()).inflate(R.layout.item_marker,null)
//    val markerFrame = markerView.findViewById<ConstraintLayout>(R.id.marker_frame)
//    val markerIv =  markerView.findViewById<ImageView>(R.id.marker_iv)

    override fun onBeforeClusterItemRendered(item: UserPhoto, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        val bitmap = BitmapFactory.decodeFile(item.uri)
        val scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        markerOptions.position(item.position).icon(BitmapDescriptorFactory.fromBitmap(scaled))
    }
}
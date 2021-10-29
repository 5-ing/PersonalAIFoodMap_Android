package com.example.personalaifoodmap.ui.activity

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.personalaifoodmap.viewmodels.FoodMapViewModel
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.example.personalaifoodmap.databinding.ActivityFoodMapBinding
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.ui.MarkerRenderer
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.viewmodels.FoodMapViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import java.util.ArrayList

internal class FoodMapActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var binding: ActivityFoodMapBinding
    private val foodMapViewModel : FoodMapViewModel by viewModels(){
        FoodMapViewModelFactory((application as FoodMapApplication).foodMapRepository)
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var clusterManager: ClusterManager<UserPhoto>
    lateinit var mapFragment:SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startFoodMap()
    }

    fun startFoodMap(){
        mapFragment = supportFragmentManager.findFragmentById(R.id.foodMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // 지도 연결
        this.mGoogleMap = googleMap

        // 현재 위치 표시
        setCurrentLocation()

        // 클러스터링
        clusterManager = ClusterManager(this, mGoogleMap)
        mGoogleMap.setOnCameraIdleListener(clusterManager)
        clickCluster()
        clickClusterItem()

        // 마커 표시
        val markerRenderer = MarkerRenderer( this, mGoogleMap, clusterManager)
        clusterManager.renderer = markerRenderer

        foodMapViewModel.userFoodPhotos.observe(this, Observer {
            for(userPhoto in it){
                clusterManager.addItem(userPhoto)
            }
        })

    }

    fun clickCluster(){
        clusterManager.setOnClusterClickListener {
            val intent = Intent( this,ClusterListActivity::class.java )
            val clusterList = arrayListOf<UserPhoto>()
            for (photoData in it.items) {
                clusterList.add(photoData)
            }
            intent.putExtra("clusterList", clusterList)
            startActivity(intent)
            false
        }
    }

    fun clickClusterItem(){
        clusterManager.setOnClusterItemClickListener{
            val intent = Intent(this,RestaurantDetailActivity::class.java)
            intent.putExtra("resDetail", it)
            startActivity(intent)
            false
        }
    }

    fun setCurrentLocation(){
        checkLocationPermission()
        val locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    currentLocation = it.lastLocation
                    drawCurrentLocation()
                }
            }
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        mGoogleMap.isMyLocationEnabled = true
    }

    fun drawCurrentLocation(){
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14f))
    }

    fun checkLocationPermission(){
        val permissionCode = 101
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
    }

}
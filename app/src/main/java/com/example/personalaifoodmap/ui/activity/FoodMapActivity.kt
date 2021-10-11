package com.example.personalaifoodmap.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.personalaifoodmap.viewmodels.FoodMapViewModel
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.personalaifoodmap.databinding.ActivityFoodMapBinding
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.viewmodels.FoodMapViewModelFactory
import com.example.personalaifoodmap.viewmodels.GallerySyncViewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import java.net.URI

internal class FoodMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityFoodMapBinding
    private val foodMapViewModel : FoodMapViewModel by viewModels(){
        FoodMapViewModelFactory((application as FoodMapApplication).foodMapRepository)
    }
    private lateinit var mNaverMap: NaverMap
    private lateinit var mLocationSource : FusedLocationSource
    private lateinit var markerView: View

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapView = binding.foodMapView
        mapView.onCreate(savedInstanceState)

        markerView = LayoutInflater.from(this).inflate(R.layout.marker_item,null)

        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 naverMap 객체를 받음
        mapView.getMapAsync(this)
    }

    fun getCustomMarker(uri : String): View{
        val markerFrame = markerView.findViewById<ConstraintLayout>(R.id.marker_frame)
        val markerIv =  markerView.findViewById<ImageView>(R.id.marker_iv)
        markerIv.setImageURI(uri.toUri())

        return markerFrame
    }

    override fun onMapReady(naverMap: NaverMap) {

        this.mNaverMap = naverMap

        // 권한확인. 결과는 onRequestPermissionsResult 콜백 매서드 호출
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE)

        // 현재위치 표시
        mLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        mNaverMap.locationSource = mLocationSource

        // 지도상에 마커 표시 (이미지의 위치 정보 기반)
        foodMapViewModel.getFoodPhotoLocation().observe(owner = this) { photos ->
            // import androidx.lifecycle.observe 를 추가해야 owner 부분 오류 안뜸
            photos.let {
                for (userPhoto in photos) {
                    val x: Float = userPhoto.lat
                    val y: Float = userPhoto.lon
                    // 초기값인 0이 아니면 마커 표시
                    if (x != 0f && y != 0f) {
                        val marker = Marker()
                        marker.position = LatLng(x.toDouble(), y.toDouble())
                        marker.map = naverMap
                        marker.icon = OverlayImage.fromView(getCustomMarker(userPhoto.uri))
                        println(userPhoto.uri)
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
            }
        }
    }
}
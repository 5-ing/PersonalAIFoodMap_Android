package com.example.personalaifoodmap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodmap);

        // 지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource =
                new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }

    List<PhotoData> pDataList = new ArrayList<>();

    // 기기 내 모든 이미지 로드 (절대경로 및 위치 정보 포함)
    public void ImageReady() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            String absolutePath = cursor.getString(columnIndex);
            if(!TextUtils.isEmpty(absolutePath)) {
                PhotoData pData = new PhotoData();
                pData.uri = absolutePath;
                try {
                    ExifInterface exif = new ExifInterface(absolutePath);
                    pData.lat = getGPS(exif)[0];
                    pData.lon = getGPS(exif)[1];

                } catch (IOException e) {
                    e.printStackTrace();
                }
                pDataList.add(pData);
            }
        }
    }

    // exif에서 위도, 경도 값 추출
    public float[] getGPS(ExifInterface exif) {
        float lat, lon;
        String attLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attLatR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attLonR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if (attLat != null && attLatR != null && attLon != null && attLonR != null) {
            if (attLatR.equals("N")) {
                lat = convertToDegree(attLat);
            } else  {
                lat = 0 - convertToDegree(attLat);
            }

            if(attLonR.equals("E")) {
                lon = convertToDegree(attLon);
            } else {
                lon = 0 - convertToDegree(attLon);
            }
        } else {
            // 위치 정보 없을 때 0으로 초기화
            lat = 0;
            lon = 0;
        }

        float[] result = new float[2];
        result[0] = lat;
        result[1] = lon;

        return result;
    }

    // exif에서 위도, 경도가 도분초로 표기. 기본 위도 경도로 변환.
    private float convertToDegree(String stringDms) {
        String[] dms = stringDms.split(",", 3);

        String[] stringD = dms[0].split("/", 2);
        double D0 = parseDouble(stringD[0]);
        double D1 = parseDouble(stringD[1]);
        double FloatD = D0 / D1;

        String[] stringM = dms[1].split("/", 2);
        double M0 = parseDouble(stringM[0]);
        double M1 = parseDouble(stringM[1]);
        double FloatM = M0 / M1;

        String[] stringS = dms[2].split("/", 2);
        double S0 = parseDouble(stringS[0]);
        double S1 = parseDouble(stringS[1]);
        double FloatS = S0 / S1;

        float result = parseFloat(String.valueOf(FloatD + (FloatM / 60) + (FloatS / 3600)));

        return result;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        ImageReady();

        // 지도상에 마커 표시 (이미지의 위치 정보 기반)
        for (PhotoData photoData : pDataList) {
            float x;
            float y;
            x = photoData.lat;
            y = photoData.lon;

            // 초기값인 0이 아니면 마커 표시
            if (x != 0 && y != 0) {
                Marker marker = new Marker();
                marker.setPosition(new LatLng(x, y));

                marker.setMap(naverMap);
                marker.setWidth(100);
                marker.setHeight(100);
                marker.setIcon(OverlayImage.fromPath(photoData.uri));
                System.out.println(photoData.uri);
            }
        }

        // 지도상에 마커 표시 (샘플 마커 3개. 삭제해도 무방.)
        Marker gmarker = new Marker();
        gmarker.setPosition(new LatLng(35.53963, 129.31149));
        gmarker.setMap(naverMap);
        gmarker.setWidth(100);
        gmarker.setHeight(100);
        gmarker.setIcon(OverlayImage.fromResource(R.drawable.yg));

        Marker hmarker = new Marker();
        hmarker.setPosition(new LatLng(37.65885, 126.77501));
        hmarker.setMap(naverMap);
        hmarker.setWidth(100);
        hmarker.setHeight(100);
        hmarker.setIcon(OverlayImage.fromResource(R.drawable.yh));

        Marker ymarker = new Marker();
        ymarker.setPosition(new LatLng(37.50703, 126.72191));
        ymarker.setMap(naverMap);
        ymarker.setWidth(100);
        ymarker.setHeight(100);
        ymarker.setIcon(OverlayImage.fromResource(R.drawable.sy));

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        // 권한확인. 결과는 onRequestPermissionsResult 콜백 매서드 호출
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }
}

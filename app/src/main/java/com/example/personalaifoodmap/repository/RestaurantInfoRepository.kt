package com.example.personalaifoodmap.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.personalaifoodmap.BuildConfig
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.data.UserPhotoDao
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ArrayList

class RestaurantInfoRepository(private val userPhotoDao: UserPhotoDao) {

    suspend fun updateFPlace(userPhoto: UserPhoto) {
        userPhotoDao.update(userPhoto)
    }

    fun getPhotoInfo(uri: String): UserPhoto{
       return userPhotoDao.getPhotoInfo(uri)
    }

    fun getRestInfo(lat: Double, lon: Double): ArrayList<FPlace> {
        val fPlaces: ArrayList<FPlace> = arrayListOf()
        try {
            val jObj = JSONObject(getJson(lat, lon))
            val meta = jObj["meta"] as JSONObject
            val size = meta["total_count"] as Int
            if (size > 0) {
                val jArray = jObj["documents"] as JSONArray
                for (i in 0 until jArray.length()) {
                    val subJobj = jArray[i] as JSONObject
                    val name = subJobj["place_name"] as String
                    val category = subJobj["category_name"] as String
                    var address = subJobj["road_address_name"] as String
                    val url = subJobj["place_url"] as String
                    if (address.isEmpty()) {
                        address = subJobj["address_name"] as String
                    }
                    val fPlace = FPlace(name, address, category, url)
                    fPlaces.add(fPlace)
                }
                return fPlaces
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fPlaces
    }

    fun getJson(lat: Double, lon: Double): String {
        val radius = 50 //50m 이내의 음식점 정보 가져오기
        val szLat = lat.toString()
        val szLon = lon.toString()
        val url =
            ("https://dapi.kakao.com/v2/local/search/category.json?category_group_code=FD6&radius=" + radius
                    + "&x=" + szLon + "&y=" + szLat + "&sort=distance")
        val response = StringBuffer()
        val auth = "KakaoAK " + BuildConfig.kakao_local_api_key
        val apiUrl = URL(url)

        var connection = apiUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("X-Requested-With", "curl")
        connection.setRequestProperty("Authorization", auth)
        connection.doOutput = true

        val responseCode = connection.responseCode
        if (responseCode == 400) {
            println("400:: 해당 명령을 실행할 수 없음")
        } else if (responseCode == 401) {
            println("401:: Authorization 잘못됨")
        } else if (responseCode == 500) {
            println("500:: 서버 에러, 문의 필요")
        } else if (responseCode == 502) {
            println("502:: Bad Gateway")
        } else if (responseCode == 503) {
            println("500:: 서비스 점검중")
        } else {
            val charset = StandardCharsets.UTF_8
            val br = BufferedReader(
                InputStreamReader(
                    connection.inputStream, charset
                )
            )
            var inputLine: String?
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
        }
        return response.toString()
    }
}
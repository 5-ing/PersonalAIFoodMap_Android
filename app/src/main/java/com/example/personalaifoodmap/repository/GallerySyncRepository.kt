package com.example.personalaifoodmap.repository

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.example.personalaifoodmap.data.UserPhotoDao
import com.example.personalaifoodmap.data.UserPhotoDatabase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.repository.GallerySyncRepository
import org.opencv.dnn.Dnn
import org.opencv.android.OpenCVLoader
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core.MinMaxLocResult
import org.opencv.utils.Converters
import androidx.room.Room
import com.example.personalaifoodmap.FoodMapApplication
import kotlinx.coroutines.flow.Flow
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.dnn.Net
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class GallerySyncRepository(private val userPhotoDao: UserPhotoDao) {

    val userPhotos : Flow<List<UserPhoto>> = userPhotoDao.getAllPhoto()
    lateinit var tinyYolo: Net

    init {
        System.loadLibrary("opencv_java3")
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userPhoto: UserPhoto) {
        userPhoto.isFood = imageDetection(userPhoto.uri)
        val exif = ExifInterface(userPhoto.uri)
        userPhoto.lat = getGPS(exif)[0]
        userPhoto.lon = getGPS(exif)[1]

        Log.e("uri ", userPhoto.uri)

        userPhotoDao.insert(userPhoto)
    }

    //모델 경로 불러오는 코드
    fun getPath(file: String): String {
        val assetManager = FoodMapApplication.applicationContext().assets
        var inputStream: BufferedInputStream? = null
        try {
            inputStream = BufferedInputStream(assetManager.open(file))
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            inputStream.close()
            // Create copy file in storage.
            val outFile = File(FoodMapApplication.applicationContext().filesDir, file)
            val os = FileOutputStream(outFile)
            os.write(data)
            os.close()
            return outFile.absolutePath
        } catch (ex: IOException) {
            Log.i(ContentValues.TAG, "Failed to upload a file")
        }
        return ""
    }

    //음식 검출코드
    fun imageDetection(url: String): Boolean {
        val tinyYoloCfg = getPath("yolov3-tiny.cfg")
        val tinyYoloWeights = getPath("yolov3-tiny.weights")
        tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights)
        var isFood = false
        val mat = Mat()
        val bitmap = BitmapFactory.decodeFile(url)
        OpenCVLoader.initDebug()
        if (bitmap == null) {
            return false
        }
        Utils.bitmapToMat(bitmap, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2BGR)
        val imageBlob = Dnn.blobFromImage(
            mat,
            0.00392,
            Size(416.0, 416.0),
            Scalar(0.0, 0.0, 0.0),  /*swapRB*/
            false,  /*crop*/
            false
        )
        tinyYolo.setInput(imageBlob)
        val result: List<Mat> = ArrayList(2)
        val outBlobNames: MutableList<String> = mutableListOf()
        outBlobNames.add(0, "yolo_16")
        outBlobNames.add(1, "yolo_23")
        tinyYolo.forward(result, outBlobNames)
        val confThreshold = 0.3f
        val clsIds: MutableList<Int> = ArrayList()
        val confs: MutableList<Float> = ArrayList()
        val rects: MutableList<Rect2d> = ArrayList()
        for (i in result.indices) {
            val level = result[i]
            for (j in 0 until level.rows()) {
                val row = level.row(j)
                val scores = row.colRange(5, level.cols())
                val mm = Core.minMaxLoc(scores)
                val confidence = mm.maxVal.toFloat()
                val classIdPoint = mm.maxLoc
                if (confidence > confThreshold) {
                    val centerX = (row[0, 0][0] * mat.cols()).toInt()
                    val centerY = (row[0, 1][0] * mat.rows()).toInt()
                    val width = (row[0, 2][0] * mat.cols()).toInt()
                    val height = (row[0, 3][0] * mat.rows()).toInt()
                    val left = centerX - width / 2
                    val top = centerY - height / 2
                    clsIds.add(classIdPoint.x.toInt())
                    confs.add(confidence)
                    rects.add(
                        Rect2d(
                            left.toDouble(),
                            top.toDouble(),
                            width.toDouble(),
                            height.toDouble()
                        )
                    )
                }
            }
        }
        val arrayLength = confs.size
        if (arrayLength >= 1) {
            val nmsThresh = 0.1f
            val confidences = MatOfFloat(Converters.vector_float_to_Mat(confs))
            val boxesArray = rects.toTypedArray()
            val bboxes = MatOfRect2d(*boxesArray)
            val indices = MatOfInt()
            Dnn.NMSBoxes(bboxes, confidences, confThreshold, nmsThresh, indices)
            isFood = true
        }
        return isFood
    }

    // exif에서 위도, 경도 값 추출
    fun getGPS(exif: ExifInterface): FloatArray {
        val lat: Float
        val lon: Float
        val attLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val attLatR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val attLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val attLonR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        if (attLat != null && attLatR != null && attLon != null && attLonR != null) {
            lat = if (attLatR == "N") {
                convertToDegree(attLat)
            } else {
                0 - convertToDegree(attLat)
            }
            lon = if (attLonR == "E") {
                convertToDegree(attLon)
            } else {
                0 - convertToDegree(attLon)
            }
        } else {
            // 위치 정보 없을 때 0으로 초기화
            lat = 0f
            lon = 0f
        }
        val result = FloatArray(2)
        result[0] = lat
        result[1] = lon
        return result
    }

    // exif에서 위도, 경도가 도분초로 표기. 기본 위도 경도로 변환.
    fun convertToDegree(stringDms: String): Float {

        val dms = stringDms.split(",")
        val stringD = dms[0].split("/")

        val D0 = stringD[0].toDouble()
        val D1 = stringD[1].toDouble()
        val FloatD = D0 / D1

        val stringM = dms[1].split("/")
        val M0 = stringM[0].toDouble()
        val M1 = stringM[1].toDouble()
        val FloatM = M0 / M1

        val stringS = dms[2].split("/")
        val S0 = stringS[0].toDouble()
        val S1 = stringS[1].toDouble()
        val FloatS = S0 / S1

        return (FloatD + FloatM / 60 + FloatS / 3600).toString().toFloat()
    }
}


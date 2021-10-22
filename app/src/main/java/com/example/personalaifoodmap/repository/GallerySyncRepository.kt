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
    val tinyYoloCfg = getPath("yolov3-tiny.cfg")
    val tinyYoloWeights = getPath("yolov3-tiny.weights")

    init {
        System.loadLibrary("opencv_java3")
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userPhoto: UserPhoto) {
        userPhoto.foodName = imageDetection(userPhoto.uri)
        userPhoto.isFood = userPhoto.foodName != ""
        val exif = ExifInterface(userPhoto.uri)
        userPhoto.lat = getGPS(exif)[0]
        userPhoto.lon = getGPS(exif)[1]

        Log.e("uri ", userPhoto.uri + userPhoto.foodName)

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
    fun imageDetection(url: String): String {
        tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights)
        var isFood = false
        val mat = Mat()
        val bitmap = BitmapFactory.decodeFile(url)
        OpenCVLoader.initDebug()
        if (bitmap == null) {
            return ""
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
        val foodNames: List<String> = listOf(
            "rice",
            "eels on rice",
            "pilaf",
            "chicken-'n'-egg on rice",
            "pork cutlet on rice",
            "beef curry",
            "sushi",
            "chicken rice",
            "fried rice",
            "tempura bowl",
            "bibimbap",
            "toast",
            "croissant",
            "roll bread",
            "raisin bread",
            "chip butty",
            "hamburger",
            "pizza",
            "sandwiches",
            "udon noodle",
            "tempura udon",
            "soba noodle",
            "ramen noodle",
            "beef noodle",
            "tensin noodle",
            "fried noodle",
            "spaghetti",
            "Japanese-style pancake",
            "takoyaki",
            "gratin",
            "sauteed vegetables",
            "croquette",
            "grilled eggplant",
            "sauteed spinach",
            "vegetable tempura",
            "miso soup",
            "potage",
            "sausage",
            "oden",
            "omelet",
            "ganmodoki",
            "jiaozi",
            "stew",
            "teriyaki grilled fish",
            "fried fish",
            "grilled salmon",
            "salmon meuniere",
            "sashimi",
            "grilled pacific saury",
            "sukiyaki",
            "sweet and sour pork",
            "lightly roasted fish",
            "steamed egg hotchpotch",
            "tempura",
            "fried chicken",
            "sirloin cutlet",
            "nanbanzuke",
            "boiled fish",
            "seasoned beef with potatoes",
            "hambarg steak",
            "steak",
            "dried fish",
            "ginger pork saute",
            "spicy chili-flavored tofu",
            "yakitori",
            "cabbage roll",
            "omelet",
            "egg sunny-side up",
            "natto",
            "cold tofu",
            "egg roll",
            "chilled noodle",
            "stir-fried beef and peppers",
            "simmered pork",
            "boiled chicken and vegetables",
            "sashimi bowl",
            "sushi bowl",
            "fish-shaped pancake with bean jam",
            "shrimp with chill source",
            "roast chicken",
            "steamed meat dumpling",
            "omelet with fried rice",
            "cutlet curry",
            "spaghetti meat sauce",
            "fried shrimp",
            "potato salad",
            "green salad",
            "macaroni salad",
            "Japanese tofu and vegetable chowder",
            "pork miso soup",
            "chinese soup",
            "beef bowl",
            "kinpira-style sauteed burdock",
            "rice ball",
            "pizza toast",
            "dipping noodles",
            "hot dog",
            "french fries",
            "mixed rice",
            "goya chanpuru",
            "green curry",
            "okinawa soba",
            "mango pudding",
            "almond jelly",
            "jjigae",
            "dak galbi",
            "dry curry",
            "kamameshi",
            "rice vermicelli",
            "paella",
            "tanmen",
            "kushikatu",
            "yellow curry",
            "pancake",
            "champon",
            "crape",
            "tiramisu",
            "waffle",
            "rare cheese cake",
            "shortcake",
            "chop suey",
            "twice cooked pork",
            "mushroom risotto",
            "samul",
            "zoni",
            "french toast",
            "fine white noodles",
            "minestrone",
            "pot au feu",
            "chicken nugget",
            "namero",
            "french bread",
            "rice gruel",
            "broiled eel bowl",
            "clear soup",
            "yudofu",
            "mozuku",
            "inarizushi",
            "pork loin cutlet",
            "pork fillet cutlet",
            "chicken cutlet",
            "ham cutlet",
            "minced meat cutlet",
            "thinly sliced raw horsemeat",
            "bagel",
            "scone",
            "tortilla",
            "tacos",
            "nachos",
            "meat loaf",
            "scrambled egg",
            "rice gratin",
            "lasagna",
            "Caesar salad",
            "oatmeal",
            "fried pork dumplings served in soup",
            "oshiruko",
            "muffin",
            "popcorn",
            "cream puff",
            "doughnut",
            "apple pie",
            "parfait",
            "fried pork in scoop",
            "lamb kebabs",
            "dish consisting of stir-fried potato, eggplant and green pepper",
            "roast duck",
            "hot pot",
            "pork belly",
            "xiao long bao",
            "moon cake",
            "custard tart",
            "beef noodle soup",
            "pork cutlet",
            "minced pork rice",
            "fish ball soup",
            "oyster omelette",
            "glutinous oil rice",
            "trunip pudding",
            "stinky tofu",
            "lemon fig jelly",
            "khao soi",
            "Sour prawn soup",
            "Thai papaya salad",
            "boned, sliced Hainan-style chicken with marinated rice",
            "hot and sour, fish and vegetable ragout",
            "stir-fried mixed vegetables",
            "beef in oyster sauce",
            "pork satay",
            "spicy chicken salad",
            "noodles with fish curry",
            "Pork Sticky Noodles",
            "Pork with lemon",
            "stewed pork leg",
            "charcoal-boiled pork neck",
            "fried mussel pancakes",
            "Deep Fried Chicken Wing",
            "Barbecued red pork in sauce with rice",
            "Rice with roast duck",
            "Rice crispy pork",
            "Wonton soup",
            "Chicken Rice Curry With Coconut",
            "Crispy Noodles",
            "Egg Noodle In Chicken Yellow Curry",
            "coconut milk soup",
            "pho",
            "Hue beef rice vermicelli soup",
            "Vermicelli noodles with snails",
            "Fried spring rolls",
            "Steamed rice roll",
            "Shrimp patties",
            "ball shaped bun with pork",
            "Coconut milk-flavored crepes with shrimp and beef",
            "Small steamed savory rice pancake",
            "Glutinous Rice Balls",
            "loco moco",
            "haupia",
            "malasada",
            "laulau",
            "spam musubi",
            "oxtail soup",
            "adobo",
            "lumpia",
            "brownie",
            "churro",
            "jambalaya",
            "nasi goreng",
            "ayam goreng",
            "ayam bakar",
            "bubur ayam",
            "gulai",
            "laksa",
            "mie ayam",
            "mie goreng",
            "nasi campur",
            "nasi padang",
            "nasi uduk",
            "babi guling",
            "kaya toast",
            "bak kut teh",
            "curry puff",
            "chow mein",
            "zha jiang mian",
            "kung pao chicken",
            "crullers",
            "eggplant with garlic sauce",
            "three cup chicken",
            "bean curd family style",
            "salt & pepper fried shrimp with shell",
            "baked salmon",
            "braised pork meat ball with napa cabbage",
            "winter melon soup",
            "steamed spareribs",
            "chinese pumpkin pie",
            "eight treasure rice",
            "hot & sour soup"
        )
        val arrayLength = confs.size
        if (arrayLength >= 1) {
            val nmsThresh = 0.1f
            val confidences = MatOfFloat(Converters.vector_float_to_Mat(confs))
            val boxesArray = rects.toTypedArray()
            val bboxes = MatOfRect2d(*boxesArray)
            val indices = MatOfInt()
            Dnn.NMSBoxes(bboxes, confidences, confThreshold, nmsThresh, indices)
            isFood = true
            return foodNames[clsIds[0]]
        }
        return ""
    }

    // exif에서 위도, 경도 값 추출
    fun getGPS(exif: ExifInterface): DoubleArray {
        val lat: Double
        val lon: Double
        val attLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val attLatR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val attLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val attLonR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        if (attLat != null && attLatR != null && attLon != null && attLonR != null) {
            lat = if (attLatR == "N") {
                convertToDegree(attLat).toDouble()
            } else {
                (0 - convertToDegree(attLat)).toDouble()
            }
            lon = if (attLonR == "E") {
                convertToDegree(attLon).toDouble()
            } else {
                (0 - convertToDegree(attLon)).toDouble()
            }
        } else {
            // 위치 정보 없을 때 0으로 초기화
            lat = 0.0
            lon = 0.0
        }

        val result = DoubleArray(2)
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


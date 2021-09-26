package com.example.personalaifoodmap

import android.app.Application
import android.content.Context
import com.example.personalaifoodmap.data.UserPhotoDatabase
import com.example.personalaifoodmap.repository.FoodGalleryRepository
import com.example.personalaifoodmap.repository.FoodMapRepository
import com.example.personalaifoodmap.repository.GallerySyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FoodMapApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { UserPhotoDatabase.getDatabase(this, applicationScope) }

    val foodGalleryRepository by lazy { FoodGalleryRepository(database.userPhotoDao()) }
    val foodMapRepository by lazy { FoodMapRepository(database.userPhotoDao()) }
    val gallerySyncRepository by lazy { GallerySyncRepository(database.userPhotoDao()) }

    init{
        instance = this
    }

    companion object {
        lateinit var instance: FoodMapApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}
package com.example.personalaifoodmap.repository

import android.app.Application
import androidx.annotation.WorkerThread
import com.example.personalaifoodmap.data.UserPhotoDao
import androidx.lifecycle.LiveData
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.data.UserPhotoDatabase
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class FoodGalleryRepository(private val userPhotoDao: UserPhotoDao) {

    val userPhotos : Flow<List<UserPhoto>> = userPhotoDao.getAllPhoto()

}
package com.example.personalaifoodmap.repository

import android.app.Application
import androidx.annotation.WorkerThread
import com.example.personalaifoodmap.data.UserPhotoDao
import androidx.lifecycle.LiveData
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.data.UserPhotoDatabase
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class FoodMapRepository(private val userPhotoDao: UserPhotoDao) {

    fun getFoodPhotoLocation() : Flow<List<UserPhoto>>{
        return userPhotoDao.getFoodPhotoLocation(true, 0f, 0f)
    }
}
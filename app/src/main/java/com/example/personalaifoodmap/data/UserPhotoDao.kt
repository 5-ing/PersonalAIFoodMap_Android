package com.example.personalaifoodmap.data

import com.example.personalaifoodmap.data.UserPhoto
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPhotoDao {
    @Insert
    suspend fun insert(userPhoto: UserPhoto)

    @Update
    suspend fun update(userPhoto: UserPhoto)

    @Delete
    suspend fun delete(userPhoto: UserPhoto)

    @Query("DELETE FROM user_photo_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_photo_table")
    fun getAllPhoto() : Flow<List<UserPhoto>>

    @Query("SELECT * FROM user_photo_table WHERE isFood = :isFood")
    fun getFoodPhoto(isFood: Boolean?): Flow<List<UserPhoto>>

    @Query("SELECT * FROM user_photo_table WHERE isFood = :isFood AND NOT lat =:lat AND NOT lon =:lon")
    fun getFoodPhotoLocation(isFood: Boolean?, lat: Float, lon: Float): Flow<List<UserPhoto>>
}
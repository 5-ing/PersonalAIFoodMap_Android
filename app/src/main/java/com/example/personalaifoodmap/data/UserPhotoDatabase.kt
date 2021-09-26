package com.example.personalaifoodmap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.example.personalaifoodmap.data.UserPhoto
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.personalaifoodmap.data.UserPhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserPhoto::class], version = 1)
abstract class UserPhotoDatabase : RoomDatabase() {
    abstract fun userPhotoDao(): UserPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: UserPhotoDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): UserPhotoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserPhotoDatabase::class.java,
                    "user_photo_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(UserPhotoDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class UserPhotoDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.userPhotoDao())
                    }
                }
            }

            suspend fun populateDatabase(userPhotoDao: UserPhotoDao) {

                //처음 삽입
//                userPhotoDao.deleteAll()
//
//                var word = Word("Hello")
//                wordDao.insert(word)
//                word = Word("World!")
//                wordDao.insert(word)

            }
        }
    }
}
package com.example.personalaifoodmap.viewmodels
import android.content.ContentValues.TAG
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.repository.GallerySyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GallerySyncViewModel (
    private val repository: GallerySyncRepository
    ) : ViewModel() {


    val syncCompletion = MutableLiveData<Boolean>()

    fun gallerySync(){
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                getGalleryCursor()
            }.getOrNull()?.let{
                syncCompletion.postValue(true)
            } ?: syncCompletion.postValue(true)
        }
    }

    fun insert(userPhoto: UserPhoto) = viewModelScope.launch{
        repository.insert(userPhoto)
    }

    fun insertUserPhotoURI(uri : String) {
        insert(UserPhoto(uri = uri, isFood = false, foodName = "", lat = 0.toDouble(), lon = 0.toDouble(), fPlace = FPlace("","","","")))
    }

    fun getGalleryCursor() {
        val cursor = FoodMapApplication.applicationContext().contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                uri?.let {
                    insertUserPhotoURI(uri)
                }
            }
            cursor.close()
        }
    }
}

class GallerySyncViewModelFactory(private val repository: GallerySyncRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GallerySyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GallerySyncViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
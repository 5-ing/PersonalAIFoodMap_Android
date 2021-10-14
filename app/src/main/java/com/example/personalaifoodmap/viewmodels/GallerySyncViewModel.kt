package com.example.personalaifoodmap.viewmodels
import android.provider.MediaStore
import androidx.lifecycle.*
import com.example.personalaifoodmap.Event
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.repository.GallerySyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GallerySyncViewModel (
    private val repository: GallerySyncRepository
    ) : ViewModel() {

    val userFoodPhotos: LiveData<List<UserPhoto>> = repository.userPhotos.asLiveData()
    private val _showCompleteMessage = MutableLiveData<Event<Boolean>>()
    val showCompleteMessage: LiveData<Event<Boolean>> = _showCompleteMessage

    fun startSync(){
        CoroutineScope(IO).launch {
            getGalleryCursor()
            withContext(Main){
                _showCompleteMessage.value = Event(true)
            }
        }
    }

    fun insert(userPhoto: UserPhoto) = viewModelScope.launch{
        repository.insert(userPhoto)
    }

    fun insertUserPhotoURI(uri : String) {
        insert(UserPhoto(uri = uri, isFood = false, foodName = "", lat = 0.toFloat(), lon = 0.toFloat()))
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
                uri?.let { insertUserPhotoURI(uri) }
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
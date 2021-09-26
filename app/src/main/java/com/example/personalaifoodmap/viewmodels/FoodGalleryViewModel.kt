package com.example.personalaifoodmap.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.personalaifoodmap.repository.FoodGalleryRepository
import com.example.personalaifoodmap.data.UserPhoto

class FoodGalleryViewModel (
    private val repository: FoodGalleryRepository
    ) : ViewModel() {

    //음식 사진을 갤러리어댑터에 넣기
    val userFoodPhotos: LiveData<List<UserPhoto>> = repository.userPhotos.asLiveData()

}

class FoodGalleryViewModelFactory(private val repository: FoodGalleryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodGalleryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodGalleryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
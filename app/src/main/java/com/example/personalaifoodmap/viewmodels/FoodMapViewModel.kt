package com.example.personalaifoodmap.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.personalaifoodmap.repository.FoodMapRepository
import com.example.personalaifoodmap.data.UserPhoto

class FoodMapViewModel (
    private val repository: FoodMapRepository
    ) : ViewModel() {

    //val userFoodPhotos: LiveData<List<UserPhoto>> = repository.userPhotos.asLiveData()

    //음식 사진만을 지도의 핀에 넣는 역할
    fun getFoodPhotoLocation() : LiveData<List<UserPhoto>>{
        return repository.getFoodPhotoLocation().asLiveData()
    }
}

class FoodMapViewModelFactory(private val repository: FoodMapRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodMapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodMapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

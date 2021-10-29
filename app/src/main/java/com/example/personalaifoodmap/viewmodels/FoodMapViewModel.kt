package com.example.personalaifoodmap.viewmodels

import androidx.lifecycle.*
import com.example.personalaifoodmap.repository.FoodMapRepository
import com.example.personalaifoodmap.data.UserPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FoodMapViewModel (
    private val foodMapRepository: FoodMapRepository
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getFoodPhotoLocation()
        }
    }

    val userFoodPhotosCompletion = MutableLiveData<Boolean>()

    val userFoodPhotos : MutableLiveData<List<UserPhoto>> = MutableLiveData()

    suspend fun getFoodPhotoLocation(){
        foodMapRepository.getFoodPhotoLocation().collect{
            userFoodPhotos.postValue(it)
        }
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

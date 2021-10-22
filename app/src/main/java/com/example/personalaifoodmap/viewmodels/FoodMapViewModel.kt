package com.example.personalaifoodmap.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.repository.FoodMapRepository
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.ui.MarkerRenderer
import com.example.personalaifoodmap.ui.activity.FoodMapActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodMapViewModel (
    private val foodMapRepository: FoodMapRepository
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getFoodPhotoLocation()
        }
    }

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
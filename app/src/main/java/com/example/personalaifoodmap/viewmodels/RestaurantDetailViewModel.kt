package com.example.personalaifoodmap.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.repository.RestaurantInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantDetailViewModel(
    private val restaurantInfoRepository: RestaurantInfoRepository
): ViewModel() {

    fun updateFPlace(userPhoto: UserPhoto) = viewModelScope.launch{
        restaurantInfoRepository.updateFPlace(userPhoto)
    }

    fun getPhotoInfo(uri: String) : MutableLiveData<UserPhoto>{
        val photoInfo : MutableLiveData<UserPhoto> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                photoInfo.postValue(restaurantInfoRepository.getPhotoInfo(uri))
            }
        }
        return photoInfo
    }

    fun getRestaurantTitleInfo(lat: Double, lon: Double) :MutableLiveData<List<FPlace>>{
        val resInfo : MutableLiveData<List<FPlace>> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                resInfo.postValue(getRestaurantInfo(lat,lon))
            }
        }
        return resInfo
    }

    fun getRestaurantInfo(lat: Double, lon: Double) : ArrayList<FPlace> {
        val fPlaces = restaurantInfoRepository.getRestInfo(lat, lon)
        return if(fPlaces.size > 0){
            restaurantInfoRepository.getRestInfo(lat, lon)
        } else{
            fPlaces.add(FPlace("알 수 없음","주변에 존재하는 식당이 없습니다","",""))
            fPlaces
        }
    }
}

class RestaurantDetailViewModelFactory(private val repository: RestaurantInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
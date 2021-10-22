package com.example.personalaifoodmap.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.repository.RestaurantInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ClusterListViewModel(
    private val restaurantInfoRepository: RestaurantInfoRepository
): ViewModel() {

    fun getRestaurantTitleInfo(lat: Double, lon: Double) :MutableLiveData<FPlace>{
        val resInfo : MutableLiveData<FPlace> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                resInfo.postValue(getRestaurantInfo(lat,lon))
            }
        }
        return resInfo
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

    fun getRestaurantInfo(lat: Double, lon: Double) : FPlace {
        val fPlaces = restaurantInfoRepository.getRestInfo(lat, lon)
        if(fPlaces.size > 0){
            return restaurantInfoRepository.getRestInfo(lat, lon)[0]
        }
        else{
            return FPlace("알 수 없음","주변에 존재하는 식당이 없습니다","","")
        }
    }
}

class ClusterListViewModelFactory(private val repository: RestaurantInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClusterListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClusterListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
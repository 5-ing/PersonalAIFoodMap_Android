package com.example.personalaifoodmap.ui.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.databinding.ActivityRestaurantDetailBinding
import com.example.personalaifoodmap.viewmodels.RestaurantDetailViewModel
import com.example.personalaifoodmap.viewmodels.RestaurantDetailViewModelFactory

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private val restaurantDetailViewModel : RestaurantDetailViewModel by viewModels(){
        RestaurantDetailViewModelFactory((application as FoodMapApplication).restaurantInfoRepository)
    }
    lateinit var resDetail : UserPhoto
    lateinit var resFixBtn : ImageView
    lateinit var resLeftBtn : ImageView
    lateinit var resRightBtn : ImageView
    var resDetails : List<FPlace> = listOf(FPlace("","","",""))
    var resIndex : Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resFixBtn = binding.resFixBtn
        resLeftBtn = binding.resLeftBtn
        resRightBtn = binding.resRightBtn

        var photoInfo = intent.getSerializableExtra("resDetail") as UserPhoto //주소 가진애
        binding.foodDetailIv.setImageURI(photoInfo.uri.toUri())
        resDetail = photoInfo
        restaurantDetailViewModel.getPhotoInfo(photoInfo.uri).observe(this, {
            resDetail = it
            if(resDetail.fPlace.resName=="") nonFixResInfo()
            else fixResInfo()
        })
    }

    fun fixResInfo(){
        resFixBtn.setImageDrawable(resources.getDrawable(R.drawable.restaurant_check))
        resLeftBtn.visibility = View.INVISIBLE
        resRightBtn.visibility = View.INVISIBLE
        
        binding.restaurantNameTv.text = resDetail.fPlace.resName
        binding.restaurantCategoryTv.text = resDetail.fPlace.resCategory
        binding.restaurantAddressTv.text = resDetail.fPlace.resAddress
        
        clickNonFixBtn()
    }

    fun nonFixResInfo(){
        setResDetail()
        restaurantDetailViewModel.getRestaurantTitleInfo(resDetail.lat, resDetail.lon)
            .observe(this, {
                resDetails = it
                setResDetail()
            })
        resFixBtn.setImageDrawable(resources.getDrawable(R.drawable.restaurant_no_check))
        resLeftBtn.visibility = View.VISIBLE
        resRightBtn.visibility = View.VISIBLE
        clickResLeftBtn()
        clickResRightBtn()
        clickFixBtn()
    }
    
    fun setResDetail(){
        binding.restaurantNameTv.text = resDetails[resIndex].resName
        binding.restaurantCategoryTv.text = resDetails[resIndex].resCategory
        binding.restaurantAddressTv.text = resDetails[resIndex].resAddress
    }
    
    fun clickResLeftBtn(){
        resLeftBtn.setOnClickListener {
            if(resIndex == 0){
                resIndex = resDetails.size - 1
            }
            else if(0 < resIndex){
                resIndex -=1
            }
            setResDetail()
        }
    }
    
    fun clickResRightBtn(){
        resRightBtn.setOnClickListener {
            if(resIndex == resDetails.size - 1){
                resIndex = 0
            }
            else if(resIndex < resDetails.size - 1){
                resIndex +=1
            }
            setResDetail()
        }
    }
    
    fun clickFixBtn(){
        resFixBtn.setOnClickListener {
            resDetail.fPlace = FPlace(resDetails[resIndex].resName, resDetails[resIndex].resAddress, resDetails[resIndex].resCategory,resDetails[resIndex].resURL )
            restaurantDetailViewModel.updateFPlace(resDetail)
            fixResInfo()
        }
    }
    
    fun clickNonFixBtn(){
        resFixBtn.setOnClickListener {
            resDetail.fPlace = FPlace("", "", "", "")
            restaurantDetailViewModel.updateFPlace(resDetail)
            nonFixResInfo()
        }
    }

}
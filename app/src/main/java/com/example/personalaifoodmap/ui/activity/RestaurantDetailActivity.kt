package com.example.personalaifoodmap.ui.activity

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.databinding.ActivityRestaurantDetailBinding
import com.example.personalaifoodmap.ui.adapter.ClusterListAdapter
import com.example.personalaifoodmap.ui.adapter.RecommentListAdapter
import com.example.personalaifoodmap.viewmodels.RestaurantDetailViewModel
import com.example.personalaifoodmap.viewmodels.RestaurantDetailViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.ArrayList

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private val restaurantDetailViewModel : RestaurantDetailViewModel by viewModels(){
        RestaurantDetailViewModelFactory((application as FoodMapApplication).restaurantInfoRepository)
    }
    lateinit var resDetail : UserPhoto
    lateinit var resFixBtn : ImageView
    lateinit var resLeftBtn : ImageView
    lateinit var resRightBtn : ImageView
    lateinit var recommendSwitch: Switch
    lateinit var recommendListAdapter: RecommentListAdapter
    lateinit var currentCategoryList : List<String>
    var resDetails : List<FPlace> = listOf(FPlace("","","",""))
    var resIndex : Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resFixBtn = binding.resFixBtn
        resLeftBtn = binding.resLeftBtn
        resRightBtn = binding.resRightBtn
        recommendSwitch = binding.recommendSwitch

        val photoInfo = intent.getSerializableExtra("resDetail") as UserPhoto
        binding.foodDetailIv.setImageURI(photoInfo.uri.toUri())
        resDetail = photoInfo
        restaurantDetailViewModel.getPhotoInfo(photoInfo.uri).observe(this, {
            resDetail = it
            if(resDetail.fPlace.resName=="") nonFixResInfo()
            else fixResInfo()
        })
        resRecommend()
    }

    fun resRecommend(){
        recommendSwitch.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch){
                showRecommendList()
                recommendSwitch.setThumbResource(R.drawable.ic_v_sign)
            }
            else{
                recommendSwitch.setThumbResource(R.drawable.ic_x_sign)
                binding.recommendRv.visibility = View.INVISIBLE
                binding.noticeTv.visibility = View.VISIBLE
                binding.noneTv.visibility = View.INVISIBLE
            }
        }
    }

    fun showRecommendList(){
        binding.noticeTv.visibility = View.INVISIBLE

        val recommendCategoryList : ArrayList<FPlace> = arrayListOf()
        restaurantDetailViewModel.getRestaurantRecommendInfo(resDetail.lat, resDetail.lon)
            .observe(this, { recommendList ->
                if(recommendList[0].resName=="알 수 없음"){
                    binding.noneTv.visibility = View.VISIBLE
                }
                else{
                    binding.recommendRv.visibility = View.VISIBLE
                    binding.noneTv.visibility = View.INVISIBLE
                    for(item in recommendList){
                        val reco_category = item.resCategory.split(">")
                        var contains = 0
                        for (cate in reco_category) {
                            for (c_cate in currentCategoryList) {
                                if (cate == c_cate) {
                                    contains++
                                }
                            }
                        }
                        if (contains >= 1) {
                            recommendCategoryList.add(item)
                        }
                        Log.e(TAG,"진짜 추천할거  :" + recommendCategoryList.toString())
                    }
                    recommendListAdapter = RecommentListAdapter(this, recommendCategoryList)
                    val recommendRv = binding.recommendRv
                    recommendRv.adapter = recommendListAdapter
                    recommendRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
                }
            })
    }

    fun fixResInfo(){
        resFixBtn.setImageDrawable(resources.getDrawable(R.drawable.restaurant_check))
        resLeftBtn.visibility = View.INVISIBLE
        resRightBtn.visibility = View.INVISIBLE
        currentCategoryList = resDetail.fPlace.resCategory.split(">")
        binding.restaurantNameTv.text = resDetail.fPlace.resName
        binding.restaurantCategoryTv.text = resDetail.fPlace.resCategory
        binding.restaurantAddressTv.text = resDetail.fPlace.resAddress
        
        clickNonFixBtn()
    }

    fun nonFixResInfo(){
        setResDetail()
        restaurantDetailViewModel.getRestaurantDetailInfo(resDetail.lat, resDetail.lon)
            .observe(this, {
                resDetails = it
                currentCategoryList = it[0].resCategory.split(">")
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
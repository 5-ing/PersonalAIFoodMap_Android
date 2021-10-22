package com.example.personalaifoodmap.ui.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.databinding.ActivityClusterListBinding
import com.example.personalaifoodmap.ui.adapter.ClusterListAdapter
import com.example.personalaifoodmap.viewmodels.ClusterListViewModel
import com.example.personalaifoodmap.viewmodels.ClusterListViewModelFactory
import com.example.personalaifoodmap.viewmodels.FoodMapViewModel
import com.example.personalaifoodmap.viewmodels.FoodMapViewModelFactory
import java.util.ArrayList

class ClusterListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClusterListBinding
    private val clusterListViewModel : ClusterListViewModel by viewModels(){
        ClusterListViewModelFactory((application as FoodMapApplication).restaurantInfoRepository)
    }
    lateinit var clusterListAdapter : ClusterListAdapter
    lateinit var initClusterList : ArrayList<UserPhoto>
    lateinit var clusterList : ArrayList<UserPhoto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClusterListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showClusterList()
    }

    override fun onStart() {
        super.onStart()

        showClusterList()
    }


    fun showClusterList(){
        clusterList = intent.getSerializableExtra("clusterList") as ArrayList<UserPhoto>
        clusterListAdapter = ClusterListAdapter(this,clusterList)
        val clusterRv = binding.clusterRv
        clusterRv.adapter = clusterListAdapter
        clusterRv.layoutManager = LinearLayoutManager(this)

        for(i in 0 until clusterList.size){
            clusterListViewModel.getPhotoInfo(clusterList[i].uri).observe(this, { userPhoto ->
                if(userPhoto.fPlace.resName==""){
                    clusterListViewModel.getRestaurantTitleInfo(clusterList[i].lat, clusterList[i].lon).observe(this, {
                        clusterList[i].fPlace = it
                        clusterListAdapter.setClusterList(clusterList)
                    })
                }
                else{
                    clusterList[i] = userPhoto
                    clusterListAdapter.setClusterList(clusterList)
                }
            })
        }
    }

}

package com.example.personalaifoodmap.ui.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClusterListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showClusterList()
    }

    fun showClusterList(){
        val clusterList = intent.getSerializableExtra("clusterList") as ArrayList<UserPhoto>
        val clusterListAdapter = ClusterListAdapter(this,clusterList)
        val clusterRv = binding.clusterRv
        clusterRv.adapter = clusterListAdapter
        clusterRv.layoutManager = LinearLayoutManager(this)

        for(i in 0 until clusterList.size){
            if(clusterList[i].fPlace.resName == ""){
                clusterListViewModel.getRestaurantTitleInfo(clusterList[i].lat, clusterList[i].lon).observe(this, {
                    clusterList[i].fPlace = it
                    clusterListAdapter.setClusterList(clusterList)
                })
            }
        }
    }

}

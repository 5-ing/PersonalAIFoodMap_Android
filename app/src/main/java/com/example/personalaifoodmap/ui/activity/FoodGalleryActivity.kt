package com.example.personalaifoodmap.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.personalaifoodmap.viewmodels.FoodGalleryViewModel
import com.example.personalaifoodmap.ui.adapter.FoodGalleryAdapter
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.databinding.ActivityFoodGalleryBinding
import com.example.personalaifoodmap.viewmodels.FoodGalleryViewModelFactory
import com.example.personalaifoodmap.viewmodels.GallerySyncViewModelFactory


class FoodGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodGalleryBinding
    private val foodGalleryViewModel : FoodGalleryViewModel by viewModels(){
        FoodGalleryViewModelFactory((application as FoodMapApplication).foodGalleryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodGalleryBinding.inflate( layoutInflater )
        setContentView(binding.root)

        val recyclerView = binding.recyclerView
        val foodGalleryAdapter = FoodGalleryAdapter()
        recyclerView.adapter = foodGalleryAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        foodGalleryViewModel.userFoodPhotos.observe(owner = this) { photos ->
            // import androidx.lifecycle.observe 를 추가해야 owner 부분 오류 안뜸
            photos.let { foodGalleryAdapter.submitList(it) }
        }
    }
}
package com.example.personalaifoodmap.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.personalaifoodmap.viewmodels.GallerySyncViewModel
import com.example.personalaifoodmap.ui.activity.FoodMapActivity
import androidx.lifecycle.ViewModelProvider
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.databinding.ActivityGallerySyncBinding
import com.example.personalaifoodmap.viewmodels.GallerySyncViewModelFactory

class GallerySyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGallerySyncBinding
    private val gallerySyncViewModel : GallerySyncViewModel by viewModels() {
        GallerySyncViewModelFactory((application as FoodMapApplication).gallerySyncRepository)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGallerySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickShowGallery()
        onClickFoodMap()
        onClickGallerySync()
    }

    private fun onClickShowGallery() {
        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, FoodGalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickFoodMap() {
        binding.foodMapBtn.setOnClickListener {

        }
    }

    private fun onClickGallerySync() {
        binding.gallerySyncBtn.setOnClickListener {
            gallerySyncViewModel.startSync()
        }
    }
}
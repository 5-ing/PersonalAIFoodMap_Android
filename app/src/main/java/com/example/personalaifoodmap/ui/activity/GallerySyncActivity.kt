package com.example.personalaifoodmap.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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

        gallerySyncViewModel.gallerySync()

        gallerySyncViewModel.syncCompletion.observe(this, Observer {
            Toast.makeText(applicationContext,"갤러리 로딩 완료!", Toast.LENGTH_LONG).show()
            finish()
        })

    }

}
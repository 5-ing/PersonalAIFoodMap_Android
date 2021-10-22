package com.example.personalaifoodmap.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalaifoodmap.databinding.ActivityRestaurantDetailBinding

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
package com.example.personalaifoodmap.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.personalaifoodmap.databinding.ActivityMainBinding
import com.example.personalaifoodmap.ui.activity.MainActivity
import com.example.personalaifoodmap.ui.activity.GallerySyncActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE)
        onClickStart()
    }

    private fun onClickStart() {
        binding.startBtn.setOnClickListener { view ->
            if (!hasPermissions(view.context, PERMISSIONS)) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("권한")
                builder.setMessage("설정 또는 앱 정보에서 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, id -> finish() }.show()
            } else {
                startApp()
            }
        }
    }

    private fun startApp() {
        val intent = Intent(this, GallerySyncActivity::class.java)
        startActivity(intent)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
    }
}
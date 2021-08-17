package com.example.personalaifoodmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Boolean isFoodGallery = false;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allImageBtn = (Button)findViewById(R.id.allImageBtn);
        allImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                isFoodGallery = false;
                Intent intent = new Intent(view.getContext(), GalleryActivity.class);
                intent.putExtra("isFoodGallery", isFoodGallery);
                view.getContext().startActivity(intent);
            }
        });

        Button foodImageBtn = (Button)findViewById(R.id.foodImageBtn);
        foodImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                isFoodGallery = true;
                Intent intent = new Intent(view.getContext(), GalleryActivity.class);
                intent.putExtra("isFoodGallery", isFoodGallery);
                view.getContext().startActivity(intent);
            }
        });

    }
}

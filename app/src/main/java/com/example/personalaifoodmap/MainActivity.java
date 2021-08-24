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

    int foodGalleryMode = 1;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allImageBtn = (Button)findViewById(R.id.allImageBtn);
        allImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                foodGalleryMode = 1;
                Intent intent = new Intent(view.getContext(), GalleryActivity.class);
                intent.putExtra("isFoodGallery", foodGalleryMode);
                view.getContext().startActivity(intent);
            }
        });

        Button foodImageBtn = (Button)findViewById(R.id.foodImageBtn);
        foodImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                foodGalleryMode = 2;
                Intent intent = new Intent(view.getContext(), GalleryActivity.class);
                intent.putExtra("foodGalleryMode", foodGalleryMode);
                view.getContext().startActivity(intent);
            }
        });

        Button foodMapBtn = (Button)findViewById(R.id.foodMapBtn);
        foodMapBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                foodGalleryMode = 3;
                Intent intent = new Intent(getApplicationContext(),  GalleryActivity.class);
                intent.putExtra("foodGalleryMode", foodGalleryMode);
                view.getContext().startActivity(intent);
            }
        });

    }
}

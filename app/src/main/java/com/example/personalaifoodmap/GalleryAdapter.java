package com.example.personalaifoodmap;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

    private ArrayList<PhotoData> items;
    private Context context;

    public GalleryAdapter(Context context, ArrayList<PhotoData> uriArr){
        this.context = context;
        this.items = uriArr;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ImageView imageView = new ImageView(context);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        imageView.setPadding(2,2,2,2);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(display.widthPixels/3,display.widthPixels/3));
        Glide.with(context).load(items.get(position).uri).into(imageView);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = (String) getItem(position);
//                Intent intent = new Intent(view.getContext(), DetectionActivity.class);
//                intent.putExtra("url", url);
//                view.getContext().startActivity(intent);
//            }
//        });

        return imageView;
    }

}

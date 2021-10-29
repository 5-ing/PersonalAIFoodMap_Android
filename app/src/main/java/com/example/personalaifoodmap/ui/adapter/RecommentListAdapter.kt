package com.example.personalaifoodmap.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.FPlace
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.ui.activity.RestaurantDetailActivity
import java.util.ArrayList

class RecommentListAdapter(
    private val context : Context,
    private var recommendList: List<FPlace>
) : RecyclerView.Adapter<RecommentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant_recomend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recommendList[position]
        holder.restaurantNameTv.text = item.resName
        holder.apply {
            Glide.with(context).load(item.resURL.toUri())
                .into(holder.restaurantIV)
        }
    }

    override fun getItemCount(): Int {
        return recommendList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantNameTv: TextView = itemView.findViewById(R.id.restaurantNameTv)
        val restaurantIV: ImageView = itemView.findViewById(R.id.restaurantIV)
    }
}
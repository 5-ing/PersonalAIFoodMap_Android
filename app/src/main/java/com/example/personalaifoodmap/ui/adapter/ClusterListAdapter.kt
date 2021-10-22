package com.example.personalaifoodmap.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.personalaifoodmap.BuildConfig
import com.example.personalaifoodmap.FoodMapApplication
import com.example.personalaifoodmap.R
import com.example.personalaifoodmap.data.UserPhoto
import com.example.personalaifoodmap.ui.activity.ClusterListActivity
import com.example.personalaifoodmap.ui.activity.RestaurantDetailActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ArrayList



class ClusterListAdapter(
    private val context : Context,
    private var clusterList: ArrayList<UserPhoto>
) : RecyclerView.Adapter<ClusterListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cluster, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = clusterList[position]
        holder.clusterIV.setImageURI(item.uri.toUri())
        holder.clusterNameTv.text = item.fPlace.resName
        holder.clusterAddressTv.text = item.fPlace.resAddress
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return clusterList.size
    }

    fun setClusterList(clusterList: ArrayList<UserPhoto>){
        this.clusterList = clusterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clusterIV: ImageView = itemView.findViewById(R.id.clusterIV)
        val clusterNameTv: TextView = itemView.findViewById(R.id.restaurantNameTv)
        val clusterAddressTv: TextView = itemView.findViewById(R.id.restaurantAddressTv)

        fun bind(item: UserPhoto){
            itemView.setOnClickListener {
                val intent = Intent(FoodMapApplication.applicationContext(),RestaurantDetailActivity::class.java)
                intent.putExtra("resDetail", item)
                context.startActivity(intent)
            }
        }
    }
}
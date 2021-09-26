package com.example.personalaifoodmap.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalaifoodmap.data.UserPhoto
import com.bumptech.glide.Glide
import com.example.personalaifoodmap.R

class FoodGalleryAdapter : ListAdapter<UserPhoto, FoodGalleryAdapter.UserPhotoViewHolder>(UserPhotoComparator())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPhotoViewHolder {
        return UserPhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: UserPhotoViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class UserPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userPhotoItemView: ImageView = itemView.findViewById(R.id.galleryImage)

        fun bind(userPhoto: UserPhoto) {
            Glide.with(userPhotoItemView.context).load(userPhoto.uri).into(userPhotoItemView);
        }

        companion object {
            fun create(parent: ViewGroup): UserPhotoViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
                return UserPhotoViewHolder(view)
            }
        }
    }

    class UserPhotoComparator : DiffUtil.ItemCallback<UserPhoto>() {
        override fun areItemsTheSame(oldItem: UserPhoto, newItem: UserPhoto): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserPhoto, newItem: UserPhoto): Boolean {
            return oldItem == newItem
        }
    }

}
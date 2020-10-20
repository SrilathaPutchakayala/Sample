package com.tm.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapters {

    @BindingAdapter("bind:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (imageUrl != null && imageUrl.isNotEmpty()){
            Picasso.get().load(imageUrl).into(view)
        }
    }
}
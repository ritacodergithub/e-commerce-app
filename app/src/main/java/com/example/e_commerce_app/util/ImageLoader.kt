package com.example.e_commerce_app.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.e_commerce_app.R

object ImageLoader {

    fun load(view: ImageView, url: String?, cornerDp: Int = 0) {
        val request = Glide.with(view).load(url)
            .placeholder(R.drawable.bg_image_placeholder)
            .error(R.drawable.bg_image_placeholder)
        if (cornerDp > 0) {
            val px = (cornerDp * view.resources.displayMetrics.density).toInt()
            request.apply(RequestOptions.bitmapTransform(RoundedCorners(px))).into(view)
        } else {
            request.into(view)
        }
    }
}
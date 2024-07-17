package com.techuntried.musicplayer.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.techuntried.musicplayer.R

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("isInPlaylistIcon")
fun setIsInPlaylistIcon(imageView: ImageView, isInPlaylist: Boolean) {
    if (isInPlaylist) {
        imageView.setImageResource(R.drawable.done_icon)
    } else {
        imageView.setImageResource(R.drawable.add_icon)
    }
}

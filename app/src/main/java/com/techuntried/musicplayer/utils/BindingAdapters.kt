package com.techuntried.musicplayer.utils

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
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


@BindingAdapter("albumName")
fun setAlbumName(textView: TextView, data: String?) {
    data?.let {

        if (data.length < 12) {
            textView.text = data
        } else {
            textView.text = data.take(12) + "..."
        }
    }
}

@BindingAdapter("albumCover")
fun setAlbumCover(imageView: ShapeableImageView, albumId: Long?) {
    albumId?.let {
        val imageUri = getAlbumArtUri(it)
        Glide.with(imageView.context).load(imageUri).placeholder(R.drawable.album_icon)
            .error(R.drawable.album_icon).into(imageView)
    } ?: kotlin.run {
        imageView.setImageResource(R.drawable.album_icon)
    }
}

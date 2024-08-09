package com.techuntried.musicplayer.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
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
        Glide.with(imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.album_icon)
            .error(R.drawable.album_icon)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    val paddingInPixels =
                        imageView.context.resources.getDimensionPixelSize(R.dimen.padding_8dp)
                    imageView.setContentPadding(
                        paddingInPixels,
                        paddingInPixels,
                        paddingInPixels,
                        paddingInPixels
                    )

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Set padding to 8dp when image is successfully loaded
                    imageView.setContentPadding(0, 0, 0, 0)
                    return false
                }
            })
            .into(imageView)
    } ?: run {
        val paddingInPixels =
            imageView.context.resources.getDimensionPixelSize(R.dimen.padding_8dp)
        imageView.setContentPadding(
            paddingInPixels,
            paddingInPixels,
            paddingInPixels,
            paddingInPixels
        )
        imageView.setImageResource(R.drawable.album_icon)
    }


}

@BindingAdapter("songCover")
fun setSongCover(imageView: ShapeableImageView, albumId: Long?) {
    albumId?.let {
        val imageUri = getAlbumArtUri(it)
        Glide.with(imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.music_icon)
            .error(R.drawable.music_icon)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    val paddingInPixels =
                        imageView.context.resources.getDimensionPixelSize(R.dimen.padding_8dp)
                    imageView.setContentPadding(
                        paddingInPixels,
                        paddingInPixels,
                        paddingInPixels,
                        paddingInPixels
                    )

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Set padding to 8dp when image is successfully loaded
                    imageView.setContentPadding(0, 0, 0, 0)
                    return false
                }
            })
            .into(imageView)
    } ?: run {
        val paddingInPixels =
            imageView.context.resources.getDimensionPixelSize(R.dimen.padding_8dp)
        imageView.setContentPadding(
            paddingInPixels,
            paddingInPixels,
            paddingInPixels,
            paddingInPixels
        )
        imageView.setImageResource(R.drawable.music_icon)
    }
}


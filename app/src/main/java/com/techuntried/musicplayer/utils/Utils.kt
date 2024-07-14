package com.techuntried.musicplayer.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.techuntried.musicplayer.R

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showSnackBar(view: View, message: String) {
    val snackBar = Snackbar.make(view, message, 500)
    snackBar.setBackgroundTint(view.resources.getColor(R.color.box_background, null))
    snackBar.setTextColor(view.resources.getColor(R.color.text_color, null))
    snackBar.show()
}
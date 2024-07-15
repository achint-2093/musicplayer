package com.techuntried.musicplayer.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.techuntried.musicplayer.R

class CustomAlertDialog(context: Context) {

    private val builder: AlertDialog.Builder =
        AlertDialog.Builder(context, R.style.RoundedCornerAlertDialog)

    private val dialog: AlertDialog = builder.create()
    private val view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)

    init {
        dialog.setView(view)
        val closeButton = view.findViewById<ImageView>(R.id.closeAlertDialog)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun setTitle(title: String) {
        val titleTextView = view.findViewById<TextView>(R.id.alert_title)
        titleTextView.text = title
    }

    fun setDescription(description: String) {
        val descriptionTextView = view.findViewById<TextView>(R.id.alert_description)
        descriptionTextView.text = description
    }

    fun setButton(buttonText: String, listener: () -> Unit) {
        val button = view.findViewById<Button>(R.id.alert_button)
        button.text = buttonText
        button.setOnClickListener {
            listener()
        }
    }

    fun close() {
        dialog.dismiss()
    }

    fun show() {
        dialog.show()
    }
}
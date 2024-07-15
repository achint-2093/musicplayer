package com.techuntried.musicplayer.ui.songPicker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.musicplayer.data.models.SongPickerModel
import com.techuntried.musicplayer.databinding.SongPickerItemLayoutBinding

class SongPickerAdapter(private val clickListener: SongPickerClickListener) :
    ListAdapter<SongPickerModel, SongPickerAdapter.MyViewHolder>(SongPickerDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    fun toggleSongSelection(position: Int) {
        getItem(position).isInPlaylist = !getItem(position).isInPlaylist
        notifyItemChanged(position)
    }

    class MyViewHolder private constructor(private val binding: SongPickerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SongPickerModel,
            clickListener: SongPickerClickListener
        ) {
            binding.song = item
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                binding.root.setOnClickListener {
                    clickListener.onAddClick(item, position, binding.addButton)
                }

                binding.addButton.setOnClickListener {
                    clickListener.onAddClick(item, position, binding.addButton)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = SongPickerItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface SongPickerClickListener {
    fun onClick(position: Int)
    fun onAddClick(song: SongPickerModel, position: Int, addButton: ImageView)
}

class SongPickerDiffUtil : DiffUtil.ItemCallback<SongPickerModel>() {
    override fun areItemsTheSame(
        oldItem: SongPickerModel,
        newItem: SongPickerModel
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: SongPickerModel,
        newItem: SongPickerModel
    ): Boolean {
        return oldItem == newItem
    }

}


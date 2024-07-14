package com.techuntried.musicplayer.ui.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.musicplayer.data.models.SongModel
import com.techuntried.musicplayer.databinding.SongItemLayoutBinding

class SongsAdapter(
    private val clickListener: SongsClickListener
) :
    ListAdapter<SongModel, SongsAdapter.MyViewHolder>(SongsDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class MyViewHolder private constructor(private val binding: SongItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SongModel,
            clickListener: SongsClickListener
        ) {
            binding.song = item

            binding.root.setOnClickListener {
                clickListener.onClick()
            }

            binding.moreButton.setOnClickListener {
                clickListener.onMoreClick(item)
            }

        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = SongItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface SongsClickListener {
    fun onClick()
    fun onMoreClick(songModel: SongModel)
}

class SongsDiffUtilCallBack : DiffUtil.ItemCallback<SongModel>() {
    override fun areItemsTheSame(
        oldItem: SongModel,
        newItem: SongModel
    ): Boolean {
        return oldItem.songId == newItem.songId
    }

    override fun areContentsTheSame(
        oldItem: SongModel,
        newItem: SongModel
    ): Boolean {
        return oldItem == newItem
    }

}


package com.techuntried.musicplayer.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.databinding.PlaylistItemLayoutBinding

class PlaylistsAdapter(
    private val clickListener: PlaylistClickListener
) :
    ListAdapter<PlaylistEntity, PlaylistsAdapter.MyViewHolder>(PlayListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MyViewHolder private constructor(private val binding: PlaylistItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: PlaylistEntity,
            clickListener: PlaylistClickListener
        ) {
            binding.playList = item
            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }
            binding.moreButton.setOnClickListener {
                clickListener.onMoreClick(playlist = item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = PlaylistItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface PlaylistClickListener {
    fun onClick(playlist: PlaylistEntity)
    fun onMoreClick(playlist: PlaylistEntity)
}

class PlayListDiffUtil : DiffUtil.ItemCallback<PlaylistEntity>() {
    override fun areItemsTheSame(
        oldItem: PlaylistEntity,
        newItem: PlaylistEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PlaylistEntity,
        newItem: PlaylistEntity
    ): Boolean {
        return oldItem == newItem
    }

}


package com.techuntried.musicplayer.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.musicplayer.data.models.AlbumModel
import com.techuntried.musicplayer.databinding.AlbumItemLayoutBinding

class AlbumAdapter(
    private val clickListener: AlbumClickListener
) :
    ListAdapter<AlbumModel, AlbumAdapter.MyViewHolder>(AlbumDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class MyViewHolder private constructor(private val binding: AlbumItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: AlbumModel,
            clickListener: AlbumClickListener
        ) {
            binding.album = item
            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }


        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = AlbumItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface AlbumClickListener {
    fun onClick(album: AlbumModel)
}

class AlbumDiffUtilCallBack : DiffUtil.ItemCallback<AlbumModel>() {
    override fun areItemsTheSame(
        oldItem: AlbumModel,
        newItem: AlbumModel
    ): Boolean {
        return oldItem.albumId == newItem.albumId
    }

    override fun areContentsTheSame(
        oldItem: AlbumModel,
        newItem: AlbumModel
    ): Boolean {
        return oldItem == newItem
    }

}


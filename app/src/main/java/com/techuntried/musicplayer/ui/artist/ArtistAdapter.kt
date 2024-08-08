package com.techuntried.musicplayer.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.musicplayer.data.models.ArtistModel
import com.techuntried.musicplayer.databinding.ArtistItemLayoutBinding

class ArtistAdapter(
    private val clickListener: ArtistClickListener
) :
    ListAdapter<ArtistModel, ArtistAdapter.MyViewHolder>(ArtistDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class MyViewHolder private constructor(private val binding: ArtistItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ArtistModel,
            clickListener: ArtistClickListener
        ) {
            binding.artist = item
            binding.root.setOnClickListener {
                clickListener.onClick()
            }


        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ArtistItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface ArtistClickListener {
    fun onClick()
}

class ArtistDiffUtilCallBack : DiffUtil.ItemCallback<ArtistModel>() {
    override fun areItemsTheSame(
        oldItem: ArtistModel,
        newItem: ArtistModel
    ): Boolean {
        return oldItem.artistId == newItem.artistId
    }

    override fun areContentsTheSame(
        oldItem: ArtistModel,
        newItem: ArtistModel
    ): Boolean {
        return oldItem == newItem
    }

}


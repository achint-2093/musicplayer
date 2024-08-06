package com.techuntried.musicplayer.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techuntried.musicplayer.ui.playlists.FragmentPlaylists
import com.techuntried.musicplayer.ui.songs.FragmentSong

class TabAdapter(
    fragment: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragment,lifecycle) {

    private val fragments =
        listOf(FragmentSong(), FragmentPlaylists(), FragmentPlaylists(), FragmentPlaylists())
    private val titles = listOf("Songs", "Playlists", "Playlists", "Playlists")

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentSong()
            1 -> FragmentPlaylists()
            2 -> FragmentPlaylists()
            3 -> FragmentPlaylists()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    fun getPageTitle(position: Int) = titles[position]
}
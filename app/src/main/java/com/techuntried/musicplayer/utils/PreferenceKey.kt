package com.techuntried.musicplayer.utils


import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKey {
    val KEY_CURRENT_SONG = longPreferencesKey("KEY_CURRENT_SONG")
    val KEY_CURRENT_PLAYLIST = longPreferencesKey("KEY_CURRENT_PLAYLIST")
    val KEY_CURRENT_FILTER_DATA = stringPreferencesKey("KEY_CURRENT_FILTER_DATA")
    val KEY_FIRST_TIME = booleanPreferencesKey("KEY_FIRST_TIME")
}
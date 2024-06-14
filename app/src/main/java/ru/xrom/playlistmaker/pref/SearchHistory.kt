package ru.xrom.playlistmaker.pref

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.xrom.playlistmaker.Track
import ru.xrom.playlistmaker.recycleView.TrackAdapter

private const val HISTORY_KEY = "track_history"
private const val MAX_HISTORY_SIZE = 10
private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

class SearchHistory(
    private val preferences: SharedPreferences,
    private val adapter: TrackAdapter,
) {

    init {
        updateAdapter(preferences)
        listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

                if (key == HISTORY_KEY) {
                    updateAdapter(sharedPreferences)
                }
            }

        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun updateAdapter(sharedPreferences: SharedPreferences?) {
        val jsonTracks = sharedPreferences?.getString(HISTORY_KEY, null)
        if (jsonTracks != null) {
            val tracks = createTracksFromJson(jsonTracks)
            adapter.items.clear()
            adapter.items.addAll(tracks)
            adapter.notifyDataSetChanged()
        }
    }

    fun addTrack(newTrack: Track) {
        var trackList = ArrayList<Track>()
        val jsonTracks = preferences.getString(HISTORY_KEY, null)
        if (jsonTracks != null) {
            trackList = createTracksFromJson(jsonTracks)
            trackList.removeIf { it.trackId == newTrack.trackId }
            if (trackList.size >= MAX_HISTORY_SIZE)
                trackList.removeAt(MAX_HISTORY_SIZE - 1)
        }

        trackList.add(0, newTrack)
        preferences.edit().putString(HISTORY_KEY, createJsonFromTracks(trackList)).apply()
    }

    fun clearHistory() {
        preferences.edit().remove(HISTORY_KEY).apply()
        adapter.items.clear()
        adapter.notifyDataSetChanged()
    }

    private fun createJsonFromTracks(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTracksFromJson(json: String): ArrayList<Track> {
        val trackListType = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, trackListType)
    }
}
package ru.xrom.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.domain.model.Track


class TrackAdapter(
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<TrackViewHolder>() {
    var items = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(items[position]) }
    }

}

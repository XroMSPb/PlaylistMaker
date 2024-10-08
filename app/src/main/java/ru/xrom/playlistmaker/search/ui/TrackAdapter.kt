package ru.xrom.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.search.domain.model.Track


class TrackAdapter(
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<TrackViewHolder>() {
    var items = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(items[position]) }
    }

    fun clearItems() {
        val oldSize = itemCount
        items.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

}

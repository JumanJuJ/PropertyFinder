package com.example.PropertyFinder.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.PropertyFinder.R
import com.example.PropertyFinder.DataClass.TrendingCity

class TrendingAdapter(
    private val items: List<TrendingCity>,
    private val onClick: (TrendingCity) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    inner class TrendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.trending_image)
        val text: TextView = view.findViewById(R.id.trending_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trending_card, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val item = items[position]
        val imageUrl = item.imageUrl

        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.image)
        }

        holder.text.text = item.address

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size
}

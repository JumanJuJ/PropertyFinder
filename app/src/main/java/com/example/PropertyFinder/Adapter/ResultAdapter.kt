package com.example.PropertyFinder.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.PropertyFinder.DataClass.Result
import com.example.PropertyFinder.R
import com.bumptech.glide.Glide


class ResultAdapter(
    private val properties: List<Result>,
    private val onItemClick: (Result) -> Unit
) : RecyclerView.Adapter<ResultAdapter.PropertyViewHolder>() {

    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.propertyImage)
        val priceView: TextView = itemView.findViewById(R.id.propertyPrice)
        val typeView: TextView = itemView.findViewById(R.id.propertyType)
        val addressView: TextView = itemView.findViewById(R.id.propertyAddress)
        val localArea: TextView = itemView.findViewById(R.id.livingArea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listing_preview, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]

        val imageUrl = property.media?.allPropertyPhotos?.highResolution?.firstOrNull()
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imageView)
        }

        holder.priceView.text = "Price: $${property.price?.value ?: "N/A"}"
        holder.typeView.text = "Property Type: ${property.propertyType ?: "N/A"}"
        holder.addressView.text = "Address: ${property.address?.streetAddress ?: "N/A"}"
        holder.localArea.text = "Living Area: ${property.livingArea ?: "?"} sqft"

        holder.itemView.setOnClickListener {
            onItemClick(property)
        }




    }

    override fun getItemCount(): Int = properties.size
}

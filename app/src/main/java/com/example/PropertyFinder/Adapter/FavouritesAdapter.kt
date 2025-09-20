package com.example.PropertyFinder.Adapter

import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.PropertyFinder.DataClass.ListDetails
import com.example.PropertyFinder.R

class ListDetailsAdapter(
    private val items: List<ListDetails>,
    private val onItemClick: (ListDetails) -> Unit
) : RecyclerView.Adapter<ListDetailsAdapter.ListDetailsViewHolder>() {

    inner class ListDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.propertyImage)
        val priceView: TextView = itemView.findViewById(R.id.propertyPrice)
        val addressView: TextView = itemView.findViewById(R.id.propertyAddress)
        val typeView: TextView = itemView.findViewById(R.id.propertyType)
        val areaView: TextView = itemView.findViewById(R.id.livingArea)
        val bedroomView: TextView = itemView.findViewById(R.id.bedroomInfo)
        val bathroomView: TextView = itemView.findViewById(R.id.bathroomInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listing_details, parent, false)
        return ListDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListDetailsViewHolder, position: Int) {
        val item = items[position]
        val result = item.results

        val imageUrl = result?.media?.allPropertyPhotos?.highResolution?.firstOrNull()
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imageView)
        }

        holder.priceView.text = "Price: $${result?.price?.value ?: "N/A"}"
        holder.addressView.text =
            "Address: ${result?.address?.streetAddress ?: item.inputAddress ?: "N/A"}"
        holder.typeView.text = "Property Type: ${result?.propertyType ?: "N/A"}"
        holder.areaView.text = "Living Area: ${result?.livingArea ?: "?"} sqft"
        holder.bedroomView.text = "Beds: ${result?.bedrooms ?: "-"}"
        holder.bathroomView.text = "Baths: ${result?.bathrooms ?: "-"}"

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}

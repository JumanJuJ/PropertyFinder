package com.example.PropertyFinder.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.PropertyFinder.DataClass.AddListing
import com.example.PropertyFinder.R


class MyListingAdapter(
    private val items: List<AddListing>,
    private val onEstimateClick: (item: AddListing, position: Int) -> Unit
) : RecyclerView.Adapter<MyListingAdapter.VH>() {

    private val prices = mutableMapOf<Int, Double>()
    private val loading = mutableSetOf<Int>()

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvAddress: TextView = v.findViewById(R.id.tvAddress)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvPropertyType: TextView = v.findViewById(R.id.tvPropertyType)
        val tvLivingArea: TextView = v.findViewById(R.id.tvLivingArea)
        val tvLotSize: TextView = v.findViewById(R.id.tvLotSize)
        val tvYearBuilt: TextView = v.findViewById(R.id.tvYearBuilt)

        val tvEstimatedPrice: TextView = v.findViewById(R.id.tvEstimatedPrice)
        val btnEstimate: com.google.android.material.button.MaterialButton = v.findViewById(R.id.callModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_listings_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = items[position]

        h.tvAddress.text = "Address: ${item.address}"
        h.tvLocation.text = "Location: ${item.location}"
        h.tvPropertyType.text = "Property Type: ${item.propertyType}"
        h.tvLivingArea.text = "Living Area: ${item.livingArea} sqft"
        h.tvLotSize.text = "Lot Size: ${item.lotSize} sqft"
        h.tvYearBuilt.text = "Year Built: ${item.yearBuilt}"

        // Prezzo se disponibile
        val price = prices[position]
        if (price != null) {
            h.tvEstimatedPrice.visibility = View.VISIBLE
            h.tvEstimatedPrice.text = "Evaluated Price: $${price.toInt()}"
        } else {
            h.tvEstimatedPrice.visibility = View.GONE
        }

        // Loading / click
        val isLoading = loading.contains(position)
        h.btnEstimate.isEnabled = !isLoading
        h.btnEstimate.text = if (isLoading) h.itemView.context.getString(R.string.estimating)
        else h.itemView.context.getString(R.string.estimate_price)

        h.btnEstimate.setOnClickListener {
            onEstimateClick(item, position)
        }
    }

    override fun getItemCount() = items.size

    fun setLoading(position: Int, value: Boolean) {
        if (value) loading.add(position) else loading.remove(position)
        notifyItemChanged(position)
    }

    fun setPrice(position: Int, price: Double) {
        prices[position] = price
        loading.remove(position)
        notifyItemChanged(position)
    }
}


package com.example.PropertyFinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.Adapter.ImageSliderAdapter
import com.example.PropertyFinder.Adapter.ListDetailsAdapter
import com.example.PropertyFinder.DataClass.FavouriteResponse
import com.example.PropertyFinder.DataClass.ListDetails
import com.example.PropertyFinder.DataClass.RootListDetails
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.Utils.ErrorHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : Fragment() {

    private var map: GoogleMap? = null
    private var latLngToPlot: LatLng? = null
    val args: ProfileArgs by navArgs()
    private val userId by lazy { args.userId }
    private lateinit var favouritesAdapter: ListDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameText = view.findViewById<TextView>(R.id.username)
        val arrowButton = view.findViewById<ImageButton>(R.id.back_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.results_recycler_view)

        favouritesAdapter = ListDetailsAdapter(emptyList()) { displaySingleAddressDetails(it) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favouritesAdapter

        getFavourites(userId)

        ApiClient.client.getUsername(userId).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                if (response.isSuccessful) {
                    usernameText.text = response.body()?.username ?: "User"
                } else {
                    val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                        ?: "Error in fetching data"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                ErrorHandler.handleFailure(requireContext(), t)
            }
        })

        arrowButton.setOnClickListener {
            val action = ProfileDirections.actionProfileToHome2(userId)
            findNavController().navigate(action)
        }
    }

    private fun getFavourites(userId: String) {
        ApiClient.client.getFavourites(userId).enqueue(object : Callback<FavouriteResponse> {
            override fun onResponse(call: Call<FavouriteResponse>, response: Response<FavouriteResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    getListingLocationFromArray(result)
                } else {
                    val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                        ?: "Error in fetching data"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavouriteResponse>, t: Throwable) {
                ErrorHandler.handleFailure(requireContext(), t)
            }
        })
    }

    private fun getListingLocationFromArray(arrayIds: Array<String>?) {
        ApiClient.client.getListingLocationFromArray(arrayIds).enqueue(object : Callback<RootListDetails> {
            override fun onResponse(call: Call<RootListDetails>, response: Response<RootListDetails>) {
                if (response.isSuccessful) {
                    val listings = response.body()?.data ?: emptyList()
                    updateRecyclerView(listings)
                } else {
                    val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                        ?: "Error in fetching data"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RootListDetails>, t: Throwable) {
                ErrorHandler.handleFailure(requireContext(), t)
            }
        })
    }

    private fun updateRecyclerView(list: List<ListDetails>) {
        favouritesAdapter = ListDetailsAdapter(list) { clickedProperty ->
            val address = clickedProperty.results?.address?.streetAddress ?: clickedProperty.inputAddress ?: ""
            val action = ProfileDirections.actionProfileToShowDetails(userId, address)
            findNavController().navigate(action)
        }
        view?.findViewById<RecyclerView>(R.id.results_recycler_view)?.adapter = favouritesAdapter
    }


    fun displaySingleAddressDetails(property: ListDetails?) {
        Log.d("DEBUG", "property (ListDetails): ${Gson().toJson(property)}")

        val view = requireView()

        val imageSlider: ViewPager2 = view.findViewById(R.id.imageSlider)
        val priceView: TextView = view.findViewById(R.id.propertyPrice)
        val typeView: TextView = view.findViewById(R.id.propertyType)
        val addressView: TextView = view.findViewById(R.id.propertyAddress)
        val areaView: TextView = view.findViewById(R.id.propertyLivingArea)
        val livingStatus: TextView = view.findViewById(R.id.propertyStatus)
        val descriptionView: TextView = view.findViewById(R.id.propertyDescriptionText)
        val bathroomView: TextView = view.findViewById(R.id.bathroomInfo)
        val bedroomView: TextView = view.findViewById(R.id.bedroomInfo)
        val yearBuilt: TextView = view.findViewById(R.id.yearBuilt)
        val lotSizeView: TextView = view.findViewById(R.id.lotSize)
        val pricePerSqft: TextView = view.findViewById(R.id.pricePerSquareFoot)
        val garageView: TextView = view.findViewById(R.id.garageInfo)
        val coolingView: TextView = view.findViewById(R.id.coolingInfo)
        val heatingView: TextView = view.findViewById(R.id.heatingInfo)
        val flooringView: TextView = view.findViewById(R.id.flooring)
        val appliancesView: TextView = view.findViewById(R.id.appliances)
        val hoaView: TextView = view.findViewById(R.id.hoaInfo)
        val hoaFrequencyView: TextView = view.findViewById(R.id.hoaFrequencyInfo)

        val photoUrls = property?.results?.media?.allPropertyPhotos?.highResolution ?: emptyList()
        val sliderAdapter = ImageSliderAdapter(photoUrls)
        imageSlider.adapter = sliderAdapter

        priceView.text = "Price: $${property?.results?.price?.value ?: "N/A"}"
        typeView.text = "Type: ${property?.results?.propertyType ?: "N/A"}"
        addressView.text = "Address: ${property?.inputAddress ?: "N/A"}"
        areaView.text = "Flooring: ${property?.results?.livingArea ?: "?"} sqft"
        descriptionView.text = property?.results?.description ?: "N/A"
        bathroomView.text = "Baths: ${property?.results?.bathrooms ?: "N/A"}"
        bedroomView.text = "Beds: ${property?.results?.bedrooms ?: "N/A"}"
        livingStatus.text = "Stato: ${property?.results?.listingStatus ?: "N/A"}"
        yearBuilt.text = "Year built: ${property?.results?.yearBuilt ?: "N/A"}"
        lotSizeView.text = "Lot Size: ${property?.results?.lotSizeUnit?.lotSize ?: "N/A"} sqft"
        pricePerSqft.text = "Price per sqft: ${property?.results?.price?.pricePerSquareFoot ?: "N/A"}"
        garageView.text = "Garage: ${property?.results?.details?.garageInfo?.joinToString(", ") ?: "-"}"
        coolingView.text = "Cooling: ${property?.results?.details?.coolingInfo?.joinToString(", ") ?: "-"}"
        heatingView.text = "Heating: ${property?.results?.details?.heatingInfo?.joinToString(", ") ?: "-"}"
        flooringView.text = "Flooring: ${property?.results?.details?.flooring?.joinToString(", ") ?: "-"}"
        appliancesView.text = "Appliances: ${property?.results?.details?.appliances?.joinToString(", ") ?: "-"}"
        hoaView.text = "HOA Fee: ${property?.results?.hoaFee ?: "N/A"}"
        hoaFrequencyView.text = "Hoa Frequency: ${property?.results?.hoaFeeFrequency ?: "N/A"}"

        val lat = property?.results?.location?.latitude
        val lng = property?.results?.location?.longitude
        if (lat != null && lng != null) {
            latLngToPlot = LatLng(lat, lng)
            map?.let { plotLocationOnMap(it) }
        }
    }

    private fun plotLocationOnMap(googleMap: GoogleMap) {
        latLngToPlot?.let { location ->
            googleMap.addMarker(
                MarkerOptions().position(location).title("Position")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        latLngToPlot?.let { plotLocationOnMap(googleMap) }
    }
}

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
import androidx.viewpager2.widget.ViewPager2
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.Adapter.ImageSliderAdapter
import com.example.PropertyFinder.DataClass.Favourites
import com.example.PropertyFinder.DataClass.ListDetails
import com.example.PropertyFinder.DataClass.RootDetails
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.Utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson


class ShowDetails : Fragment(),  OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var latLngToPlot: LatLng? = null
    private val args: ShowDetailsArgs by navArgs()
    private var listingId: String? = null

    private val userId by lazy {args.userId}



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val address = args.address
        val arrowButton = view.findViewById<ImageButton>(R.id.back_button)
        val save_button = view.findViewById<ImageButton>(R.id.save_button)

        displayDetails(address)



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        Log.d("SHOW_DETAILS_ARGS", "userId = ${args.userId}, address = ${args.address}")


        arrowButton.setOnClickListener {
            findNavController().popBackStack()
        }

        save_button.setOnClickListener {
                addFavourite(userId, listingId)


        }



    }

    private fun addFavourite(userId: String, listingId: String?) {
        ApiClient.client.addFavourites(Favourites(userId, listingId))
            .enqueue(object : Callback<ServerResponse> {
                override fun onResponse(
                    call: Call<ServerResponse>,
                    response: Response<ServerResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Listing saved", Toast.LENGTH_SHORT).show()
                    }
                    else if (response.code() == 409) {
                        Toast.makeText(requireContext(), "Already saved this listing", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                            ?: "Hai già salvato questo annuncio"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    ErrorHandler.handleFailure(requireContext(), t)
                }
            })
    }


    private fun displayDetails(address: String) {
        ApiClient.client.getListingAddressDetails(address, details = true)
            .enqueue(object : Callback<RootDetails> {
                override fun onResponse(call: Call<RootDetails>, response: Response<RootDetails>) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data
                        listingId = result?._id
                        displaySingleAddressDetails(result)
                    } else {
                        val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                            ?: "Errore nel recupero dati"
                        Log.e("API_RESPONSE", "Errore: $errorMessage")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RootDetails>, t: Throwable) {
                    ErrorHandler.handleFailure(requireContext(), t)
                }
            })
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
        val lotSize = property?.results?.lotSizeUnit?.lotSize
        val lotSizeText = if (lotSize != null) "$lotSize sqft" else "N/A"
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

        addressView.text = "Address: ${property?.inputAddress}\""


        areaView.text = "Living Area: ${property?.results?.livingArea ?: "?"} sqft"
        descriptionView.text = property?.results?.description ?: "N/A"

        bathroomView.text = "Baths: ${property?.results?.bathrooms ?: "N7A"}"
        bedroomView.text = "Beds: ${property?.results?.bedrooms ?: "N/A"}"
        livingStatus.text = "Status: ${property?.results?.listingStatus?: "N/A"}\""
        yearBuilt.text = "Year built: ${property?.results?.yearBuilt?: "N/A"}"
        lotSizeView.text = "LotSize: $lotSizeText"
        pricePerSqft.text = "Price per sqft: ${property?.results?.price?.pricePerSquareFoot?: "N/A"} "
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
            map?.let {
                plotLocationOnMap(it)
            }
        }



    }

    private fun plotLocationOnMap(googleMap: GoogleMap) {
        latLngToPlot?.let { location ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Posizione immobile")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        latLngToPlot?.let {
            plotLocationOnMap(googleMap)
        }
    }



}

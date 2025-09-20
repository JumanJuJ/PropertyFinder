package com.example.PropertyFinder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.DataClass.AddListing
import com.example.PropertyFinder.DataClass.AddMyListingRequest
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.Utils.ErrorHandler
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMyListings : Fragment(R.layout.fragment_add_my_listings) {

    private val args: AddMyListingsArgs by navArgs()
    private val userId get() = args.userId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButton = view.findViewById<Button>(R.id.Confirm)
        val etAddress    = view.findViewById<TextInputEditText>(R.id.etAddress)
        val etLocation   = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etLivingArea = view.findViewById<TextInputEditText>(R.id.etLivingArea)
        val etLotSize    = view.findViewById<TextInputEditText>(R.id.etLotSize)
        val actPropType  = view.findViewById<MaterialAutoCompleteTextView>(R.id.actPropertyType)
        val etYearBuilt  = view.findViewById<TextInputEditText>(R.id.etYearBuilt)

        val items = resources.getStringArray(R.array.property_types)
        val ddAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        actPropType.setAdapter(ddAdapter)
        actPropType.setOnClickListener { actPropType.showDropDown() }
        actPropType.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) actPropType.showDropDown() }

        confirmButton.setOnClickListener {
            val address      = etAddress.text?.toString()?.trim().orEmpty()
            val location     = etLocation.text?.toString()?.trim().orEmpty()
            val livingArea   = etLivingArea.text?.toString()?.trim()?.toIntOrNull()
            val lotSizeStr   = etLotSize.text?.toString()?.trim()?.replace(',', '.')
            val lotSize      = lotSizeStr?.toDoubleOrNull()
            val propertyType = actPropType.text?.toString()?.trim().orEmpty()
            val yearBuilt    = etYearBuilt.text?.toString()?.trim()?.toIntOrNull()

            val isValid = address.isNotBlank() &&
                    location.isNotBlank() &&
                    propertyType.isNotBlank() &&
                    livingArea != null && livingArea > 0 &&
                    lotSize != null && lotSize > 0.0 &&
                    yearBuilt != null && yearBuilt in 1800..2100

            if (!isValid) {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listing = AddListing(
                address = address,
                location = location,
                livingArea = livingArea!!,
                lotSize = lotSize!!,
                propertyType = propertyType,
                yearBuilt = yearBuilt!!
            )

            val body = AddMyListingRequest(userId = userId, result = listing)

            ApiClient.client.addMyListings(body).enqueue(object : Callback<ServerResponse> {
                override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Listing published!", Toast.LENGTH_SHORT).show()
                        val action = AddMyListingsDirections.actionAddMyListingsToSell(userId)
                        findNavController().navigate(action)
                    } else {
                        val errorMessage = ErrorHandler.extractServerError(response.errorBody()) ?: "Error in fetching data"
                        Log.e("API_RESPONSE", "Errore: $errorMessage")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    ErrorHandler.handleFailure(requireContext(), t)
                }
            })
        }
    }
}

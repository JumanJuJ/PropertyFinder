package com.example.PropertyFinder

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.Adapter.MyListingAdapter
import com.example.PropertyFinder.DataClass.AddListing
import com.example.PropertyFinder.DataClass.MyListingsResponse
import com.example.PropertyFinder.DataClass.PriceRequest
import com.example.PropertyFinder.DataClass.PriceResponse
import com.example.PropertyFinder.Utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Sell : Fragment(R.layout.fragment_sell) {

    private val args: SellArgs by navArgs()
    private val userId by lazy { args.userId }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyListingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            val action = SellDirections.actionSellToHome2(userId)
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.AddListingButton).setOnClickListener {
            val action = SellDirections.actionSellToAddMyListings(userId)
            findNavController().navigate(action)
        }

        getMyListings()
    }

    private fun getMyListings() {
        ApiClient.client.getMyListings(userId)
            .enqueue(object : Callback<MyListingsResponse> {
                override fun onResponse(
                    call: Call<MyListingsResponse>,
                    response: Response<MyListingsResponse>
                ) {
                    if (!isAdded) return

                    if (response.isSuccessful && response.body()?.success == true) {
                        val rows = response.body()?.data.orEmpty()
                        val displayItems: List<AddListing> = rows.map { it.myListing }

                        adapter = MyListingAdapter(displayItems) { item, position ->
                            adapter.setLoading(position, true)

                            val city = item.location.substringBefore(',').trim()
                                .ifEmpty { item.location.trim() }

                            val req = PriceRequest(
                                livingArea = item.livingArea.toDouble(),
                                lotSize = item.lotSize,
                                lotSizeUnit = "sqft",
                                yearBuilt = item.yearBuilt,
                                propertyType = item.propertyType.lowercase(),
                                city = city
                            )

                            ApiClient.client.predictPrice(req)
                                .enqueue(object : Callback<PriceResponse> {
                                    override fun onResponse(
                                        call: Call<PriceResponse>,
                                        resp: Response<PriceResponse>
                                    ) {
                                        if (!isAdded) return
                                        val body = resp.body()
                                        if (resp.isSuccessful && body?.success == true && body.price != null) {
                                            adapter.setPrice(position, body.price)
                                        } else {
                                            adapter.setLoading(position, false)
                                            Toast.makeText(
                                                requireContext(),
                                                body?.message ?: "Impossibile stimare il prezzo",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<PriceResponse>, t: Throwable) {
                                        if (!isAdded) return
                                        adapter.setLoading(position, false)
                                        ErrorHandler.handleFailure(requireContext(), t)
                                    }
                                })
                        }

                        recyclerView.adapter = adapter
                    } else {
                        val msg = ErrorHandler.extractServerError(response.errorBody())
                            ?: "Error in fetching data"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MyListingsResponse>, t: Throwable) {
                    if (!isAdded) return
                    ErrorHandler.handleFailure(requireContext(), t)
                }
            })
    }
}

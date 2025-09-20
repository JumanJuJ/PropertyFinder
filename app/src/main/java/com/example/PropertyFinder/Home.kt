package com.example.PropertyFinder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.Adapter.ResultAdapter
import com.example.PropertyFinder.DataClass.ListAddress
import com.example.PropertyFinder.DataClass.RootAddress
import com.example.PropertyFinder.DataClass.RootResponse
import com.example.PropertyFinder.Utils.ErrorHandler
import com.example.PropertyFinder.Utils.stringParser
import android.widget.ImageButton
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.SeekBar
import com.example.PropertyFinder.Adapter.TrendingAdapter
import com.example.PropertyFinder.DataClass.TrendingCard
import com.example.PropertyFinder.DataClass.TrendingCity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.orEmpty
import kotlin.collections.toList


class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultAdapter
    private lateinit var singleView: View
    val args: HomeArgs by navArgs()
    private val userId by lazy { args.userId }

    private lateinit var trendingTitle: TextView
    private lateinit var trendingRecycler: RecyclerView

    private lateinit var trendingTitle2: TextView
    private lateinit var trendingRecycler2: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CHECK_HOME", "userId = ${args.userId}")

        val filterButton = view.findViewById<ImageButton>(R.id.filters_button)
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val buttonSell = view.findViewById<Button>(R.id.sell_button)
        val profileButton = view.findViewById<ImageButton>(R.id.profile_button)
        val arrowButton = view.findViewById<ImageButton>(R.id.back_button)
        recyclerView = view.findViewById(R.id.results_recycler_view)
        singleView = view.findViewById(R.id.single_property_view)

        trendingTitle = view.findViewById(R.id.trending_title)
        trendingRecycler = view.findViewById(R.id.trending_recycler)

        trendingTitle2 = view.findViewById(R.id.trending_title2)
        trendingRecycler2 = view.findViewById(R.id.trending_recycler2)

        val trendingCity1 = "Las Vegas"
        val trendingCity2 = "Austin"

        trendingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        trendingTitle.text = "Trending now: $trendingCity1"

        getTrendingCity(trendingCity1) { trendingList ->
            if (trendingList.isNotEmpty()) {
                trendingRecycler.adapter = TrendingAdapter(trendingList) { item ->
                    val address = item.address
                    val action = HomeDirections.actionHome2ToShowDetails(userId, address)
                    findNavController().navigate(action)

                    showTrending(false)
                }
                trendingRecycler.visibility = View.VISIBLE
            } else {
                trendingRecycler.visibility = View.GONE
            }
        }


        trendingTitle2.text = "Trending now: $trendingCity2"

        getTrendingCity(trendingCity2) { trendingList2 ->
            if (trendingList2.isNotEmpty()) {
                trendingRecycler2.adapter = TrendingAdapter(trendingList2) { item ->
                    val address = item.address
                    val action = HomeDirections.actionHome2ToShowDetails(userId, address)
                    findNavController().navigate(action)

                    showTrending(false)
                }
                trendingRecycler2.visibility = View.VISIBLE
            } else {
                trendingRecycler2.visibility = View.GONE
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query?.trim()
                if (!q.isNullOrEmpty()) {
                    singleView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    searchView.isEnabled = false
                    showTrending(false)
                    displayResults(q)
                    searchView.isEnabled = true
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    showTrending(true)
                }
                return false
            }
        })

        buttonSell.setOnClickListener {
            val action = HomeDirections.actionHome2ToSell(userId)
            findNavController().navigate(action)
        }
        profileButton.setOnClickListener {
            val action = HomeDirections.actionHome2ToProfile(userId)
            findNavController().navigate(action)
        }
        arrowButton.setOnClickListener {
            val action = HomeDirections.actionHome2ToLogin()
            findNavController().navigate(action)
        }

        filterButton.setOnClickListener {
            val q = searchView.query?.toString()?.trim().orEmpty()
            if (q.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Insert a location befor using filters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showFilters(q)
            }
        }
    }

    private fun showTrending(show: Boolean) {
        trendingTitle.visibility = if (show) View.VISIBLE else View.GONE
        trendingRecycler.visibility = if (show) View.VISIBLE else View.GONE
        trendingTitle2.visibility = if (show) View.VISIBLE else View.GONE
        trendingRecycler2.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun displayResults(q: String) {

        if (stringParser(q)) {
            addressAPI(q)
        } else {
            listingAPI(q)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.visibility = View.VISIBLE
        singleView.visibility = View.GONE
    }





    private fun listingAPI(q: String) {
        ApiClient.client.getListing(q).enqueue(object : Callback<RootResponse> {
            override fun onResponse(call: Call<RootResponse>, response: Response<RootResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    val propertyList = result?.results?.toList() ?: emptyList()
                    Log.d("TEST_CLICK", "Adapter creato. userId = $userId")

                    adapter = ResultAdapter(propertyList) { clickedProperty ->
                        val address = clickedProperty.address?.streetAddress ?: ""
                        val action = HomeDirections.actionHome2ToShowDetails(userId, address)
                        findNavController().navigate(action)
                    }

                    recyclerView.adapter = adapter
                } else {
                    val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                        ?: "Fetching..."
                    Log.e("API_RESPONSE", "Errore: $errorMessage")
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RootResponse>, t: Throwable) {
                ErrorHandler.handleFailure(requireContext(), t)
            }
        })
    }

    private fun addressAPI(q: String) {
        ApiClient.client.getListingAddress(q).enqueue(object : Callback<RootAddress> {
            override fun onResponse(call: Call<RootAddress>, response: Response<RootAddress>) {
                if (response.isSuccessful) {
                    val property = response.body()?.data
                    if (property != null) {
                        showSingle(property)
                    }
                } else {
                    val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                        ?: "Fetching..."
                    Log.e("API_RESPONSE", "Errore: $errorMessage")
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RootAddress>, t: Throwable) {
                ErrorHandler.handleFailure(requireContext(), t)
            }
        })
    }

    private fun showSingle(property: ListAddress) {
        recyclerView.visibility = View.GONE
        singleView.visibility = View.VISIBLE
        displaySingleAddress(property)
    }

    private fun displaySingleAddress(property: ListAddress) {
        val imageView: ImageView = singleView.findViewById(R.id.propertyImage)
        val priceView: TextView = singleView.findViewById(R.id.propertyPrice)
        val typeView: TextView = singleView.findViewById(R.id.propertyType)
        val addressView: TextView = singleView.findViewById(R.id.propertyAddress)
        val areaView: TextView = singleView.findViewById(R.id.livingArea)

        val imageUrl = property.results?.media?.allPropertyPhotos?.highResolution?.firstOrNull()
        if (imageUrl != null) {
            Glide.with(requireContext())
                .load(imageUrl)
                .into(imageView)
        }

        priceView.text = "$${property.results?.price?.value ?: "N/A"}"
        typeView.text = property.results?.propertyType ?: "N/A"
        addressView.text = property.inputAddress ?: "N/A"
        areaView.text = "${property.results?.livingArea ?: "?"} sqft"

        singleView.setOnClickListener {

            val address = property.inputAddress
            if (!userId.isNullOrEmpty() && !address.isNullOrEmpty()) {
                val action = HomeDirections.actionHome2ToShowDetails(userId, address)
                findNavController().navigate(action)
            } else {
                Log.e(
                    "NAVIGATION_ERROR",
                    "userId or address is null → userId=$userId, address=$address"
                )
                Toast.makeText(
                    requireContext(),
                    "Impossibile aprire i dettagli",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }


    }


    private fun showFilters(q: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.filter_sheet, null)
        dialog.setContentView(view)

        val cbCondo = view.findViewById<CheckBox>(R.id.cb_condo)
        val cbSingleFamily = view.findViewById<CheckBox>(R.id.cb_singleFamily)
        val cbMultiFamily = view.findViewById<CheckBox>(R.id.cb_multiFamily)
        val cbTownhome = view.findViewById<CheckBox>(R.id.cb_townhome)

        // Seekbar setup
        setupDualSeekbar(
            view, R.id.sb_prezzo_min, R.id.sb_prezzo_max,
            R.id.tv_price_min_val, R.id.tv_price_max_val,
            base = 0, step = 5_000, maxValue = 1_000_000, unit = "€"
        )

        setupDualSeekbar(
            view, R.id.sb_livingArea_min, R.id.sb_livingArea_max,
            R.id.tv_livingArea_min_val, R.id.tv_livingArea_max_val,
            base = 0, step = 50, maxValue = 10_000, unit = "sqft"
        )

        setupDualSeekbar(
            view, R.id.sb_lotSize_min, R.id.sb_lotSize_max,
            R.id.tv_lotSize_min_val, R.id.tv_lotSize_max_val,
            base = 0, step = 100, maxValue = 50_000, unit = "sqft"
        )

        val btnReset = view.findViewById<Button>(R.id.btn_reset)
        val btnApplica = view.findViewById<Button>(R.id.btn_applica)

        btnReset.setOnClickListener {
            cbCondo.isChecked = false
            cbSingleFamily.isChecked = false
            cbMultiFamily.isChecked = false
            cbTownhome.isChecked = false

            view.findViewById<SeekBar>(R.id.sb_prezzo_min).progress = 0
            view.findViewById<SeekBar>(R.id.sb_prezzo_max).apply { progress = max }

            view.findViewById<SeekBar>(R.id.sb_livingArea_min).progress = 0
            view.findViewById<SeekBar>(R.id.sb_livingArea_max).apply { progress = max }

            view.findViewById<SeekBar>(R.id.sb_lotSize_min).progress = 0
            view.findViewById<SeekBar>(R.id.sb_lotSize_max).apply { progress = max }
        }

        btnApplica.setOnClickListener {
            val filters = buildList {
                if (cbCondo.isChecked) add("condo")
                if (cbSingleFamily.isChecked) add("singleFamily")
                if (cbMultiFamily.isChecked) add("multiFamily")
                if (cbTownhome.isChecked) add("townhome")
            }

            val (priceMin, priceMax) = readRange(
                view,
                R.id.sb_prezzo_min,
                R.id.sb_prezzo_max,
                base = 0,
                step = 5_000
            )
            val (livingMin, livingMax) = readRange(
                view,
                R.id.sb_livingArea_min,
                R.id.sb_livingArea_max,
                base = 0,
                step = 50
            )
            val (lotMin, lotMax) = readRange(
                view,
                R.id.sb_lotSize_min,
                R.id.sb_lotSize_max,
                base = 0,
                step = 100
            )

            // invia null quando il range è “tutto”
            val pMin = if (priceMin == 0 && priceMax == 1_000_000) null else priceMin
            val pMax = if (priceMin == 0 && priceMax == 1_000_000) null else priceMax
            val laMin = if (livingMin == 0 && livingMax == 10_000) null else livingMin
            val laMax = if (livingMin == 0 && livingMax == 10_000) null else livingMax
            val lsMin = if (lotMin == 0 && lotMax == 50_000) null else lotMin
            val lsMax = if (lotMin == 0 && lotMax == 50_000) null else lotMax

            ApiClient.client.getListingFilters(
                location = q,
                filters = if (filters.isEmpty()) null else filters,
                priceMin = pMin,
                priceMax = pMax,
                livingAreaMin = laMin,
                livingAreaMax = laMax,
                lotSizeMin = lsMin,
                lotSizeMax = lsMax
            ).enqueue(object : Callback<RootResponse> {
                override fun onResponse(
                    call: Call<RootResponse>,
                    response: Response<RootResponse>
                ) {
                    if (response.isSuccessful) {
                        val propertyList = response.body()?.data?.results?.toList().orEmpty()

                        adapter = ResultAdapter(propertyList) { clickedProperty ->
                            val address = clickedProperty.address?.streetAddress ?: ""
                            val action = HomeDirections.actionHome2ToShowDetails(userId, address)
                            findNavController().navigate(action)
                        }
                        recyclerView.adapter = adapter

                        recyclerView.visibility = View.VISIBLE
                        singleView.visibility = View.GONE
                    } else {
                        val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                            ?: "Fetching..."
                        Log.e("API_RESPONSE", "Errore: $errorMessage")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RootResponse>, t: Throwable) {
                    ErrorHandler.handleFailure(requireContext(), t)
                }
            })

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupDualSeekbar(
        view: View,
        minId: Int, maxId: Int, tvMinId: Int, tvMaxId: Int,
        base: Int, step: Int, maxValue: Int, unit: String
    ) {
        val sbMin = view.findViewById<SeekBar>(minId)
        val sbMax = view.findViewById<SeekBar>(maxId)
        val tvMin = view.findViewById<TextView>(tvMinId)
        val tvMax = view.findViewById<TextView>(tvMaxId)

        val maxProgress = (maxValue - base) / step
        sbMin.max = maxProgress
        sbMax.max = maxProgress
        sbMin.progress = 0
        sbMax.progress = maxProgress

        fun p2v(p: Int) = base + p * step
        fun update() {
            tvMin.text = "${p2v(sbMin.progress)} $unit - "
            tvMax.text = "${p2v(sbMax.progress)} $unit"
        }
        update()

        sbMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > sbMax.progress) sbMax.progress = progress
                update()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        sbMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < sbMin.progress) sbMin.progress = progress
                update()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun readRange(
        view: View,
        minId: Int,
        maxId: Int,
        base: Int,
        step: Int
    ): Pair<Int, Int> {
        val minSb = view.findViewById<SeekBar>(minId)
        val maxSb = view.findViewById<SeekBar>(maxId)
        val minVal = base + minSb.progress * step
        val maxVal = base + maxSb.progress * step
        return minVal to maxVal
    }

    private fun getTrendingCity(trendingCity: String, onResult: (List<TrendingCity>) -> Unit) {
        ApiClient.client.getListingTrending(trendingCity, true)
            .enqueue(object : Callback<TrendingCard> {
                override fun onResponse(
                    call: Call<TrendingCard>,
                    response: Response<TrendingCard>
                ) {
                    if (response.isSuccessful) {
                        val trendingList = response.body()?.TrendingList ?: emptyList()
                        onResult(trendingList)
                    } else {
                        val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                            ?: "Fetching..."
                        Log.e("API_RESPONSE", "Errore: $errorMessage")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        onResult(emptyList())
                    }
                }

                override fun onFailure(call: Call<TrendingCard>, t: Throwable) {
                    ErrorHandler.handleFailure(requireContext(), t)
                    onResult(emptyList())
                }
            })


    }
}

package com.example.gruppetto

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


open class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var lastLocation: Location
    private lateinit var placesClient: PlacesClient
    private lateinit var bottomSheet: View
    private lateinit var locationListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationTextView = findViewById(R.id.current_location)
        addressTextView = findViewById(R.id.current_address)

        setUpPlaces()
        guessCurrentPlace()

        bottomSheet = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start()
            }

            override fun onStateChanged(p0: View, p1: Int) {}

        })

        locationListView = findViewById(R.id.card_listView)
        setUpLocationList()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)

        val paulSabatier = LatLng(43.562382, 1.470153)
        map.addMarker(MarkerOptions().position(paulSabatier).title("Paul Sabatier"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(paulSabatier, 17.0f))

        setUpMap()
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setUpLocationList() {
        val cardLocation: CardLocation = CardLocation("Place de Verdun", "1 Place de Verdun, 65000 TARBES, France")
        val cardLocation2: CardLocation = CardLocation("Lycée Théophile Gautier", "15 Rue Abbé Torne, 65000 Tarbes, France")

        val locationList = arrayListOf<CardLocation>()

        locationList.add(cardLocation)
        locationList.add(cardLocation2)

        val adapter = LocationAdapter(this, locationList)
        locationListView.adapter = adapter
    }

    private fun setUpPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        }

        placesClient = Places.createClient(this)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }

    private fun placeMarkerOnMap(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)

        val titleStr = getAddress(latLng)  // add these two lines
        addressTextView.text = titleStr
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && addresses.isNotEmpty()) {
                addressText =
                    addresses.get(0).getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    private fun guessCurrentPlace() {
        val placeFields: List<Place.Field> = Collections.singletonList(Place.Field.NAME)
        val currentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        var currentPlaceTask: Task<FindCurrentPlaceResponse> = placesClient.findCurrentPlace(currentPlaceRequest)
        currentPlaceTask.addOnSuccessListener { response ->
            locationTextView.text = response.placeLikelihoods[0].place.name
        }
        currentPlaceTask.addOnFailureListener { exception ->
            exception.printStackTrace()
            val toast = Toast.makeText(applicationContext, "Location recovery error", Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}
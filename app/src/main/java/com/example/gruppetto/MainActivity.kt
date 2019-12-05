package com.example.gruppetto

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gruppetto.Adapters.LocationAdapter
import com.example.gruppetto.Models.CardLocation
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


open class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    MaterialSearchBar.OnSearchActionListener, BottomNavigationView.OnNavigationItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener {

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
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userUID: String
    private lateinit var currentAddress: String
    private lateinit var currentLocation: GeoPoint
    private lateinit var currentPlace: String
    private lateinit var fab: FloatingActionButton

    private lateinit var searchBar: MaterialSearchBar
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fab = findViewById(R.id.fab)

        setupUser()
        getLocationHistory()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationTextView = findViewById(R.id.current_location)
        addressTextView = findViewById(R.id.current_address)

        setUpPlaces()
        guessCurrentPlace()
        bottomSheet = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start()
            }

            override fun onStateChanged(p0: View, p1: Int) {}

        })

        locationListView = findViewById(R.id.card_listView)

        fab.setOnClickListener {
            addNewLocation()
        }

        setUpSearchBar()
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

    private fun setUpSearchBar() {
        searchBar = findViewById(R.id.searchBar)
        drawer = findViewById(R.id.drawer_layout)

        searchBar.setOnSearchActionListener(this)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


    }

    private fun setUpLocationList(cardLocationList: ArrayList<CardLocation>) {
        val cardLocation =
            CardLocation(
                "Place de Verdun",
                "1 Place de Verdun, 65000 TARBES, France"
            )
        val cardLocation2 =
            CardLocation(
                "Lycée Théophile Gautier",
                "15 Rue Abbé Torne, 65000 Tarbes, France"
            )

        cardLocationList.add(cardLocation)
        cardLocationList.add(cardLocation2)

        val adapter = LocationAdapter(
            this,
            cardLocationList
        )
        locationListView.adapter = adapter
    }

    private fun setupUser() {
        auth = FirebaseAuth.getInstance()
        userUID = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
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
                this.currentLocation = GeoPoint(location.latitude, location.longitude)
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

    private fun getLocationHistory() {
        //get les uid dans users/currentUser/locations
        val ref = db.collection("users").document(userUID).collection("locations")
        ref.get().addOnSuccessListener { result ->
            if (result != null) {
                var locationUuidList = arrayListOf<String>()
                for (document in result) {
                    locationUuidList.add(document.id)
                }

                //get les détails des locations dans locations/
                db.collection("locations").whereIn("id",locationUuidList).get().addOnSuccessListener {result ->
                    if (result != null) {
                        var locationList = arrayListOf<CardLocation>()
                        for (document in result){
                            locationList.add(
                                CardLocation(
                                    document["title"].toString(),
                                    document["address"].toString()
                                )
                            )
                        }
                        setUpLocationList(locationList)
                    }
                }
            }
        }
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

        currentAddress = addressText
        return addressText
    }

    private fun guessCurrentPlace() {
        val placeFields: List<Place.Field> = Collections.singletonList(Place.Field.NAME)
        val currentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        var currentPlaceTask: Task<FindCurrentPlaceResponse> =
            placesClient.findCurrentPlace(currentPlaceRequest)
        currentPlaceTask.addOnSuccessListener { response ->
            currentPlace = response.placeLikelihoods[0].place.name!!
            locationTextView.text = currentPlace
        }
        currentPlaceTask.addOnFailureListener { exception ->
            exception.printStackTrace()
            val toast =
                Toast.makeText(applicationContext, "Location recovery error", Toast.LENGTH_SHORT)
            toast.show()
            currentPlace = "Default"
        }
    }

    private fun addNewLocation() {


        val refLoc = db.collection("locations").document()
        val dataLocation = hashMapOf(
            "title" to currentPlace,
            "address" to currentAddress,
            "location" to currentLocation,
            "id" to refLoc.id

        )
        val dataUser = hashMapOf(
            "time" to Date()
        )
        //a ajouter : verif si location existe déjà
        refLoc.set(dataLocation)
            .addOnSuccessListener {
                val refUser = db.collection("users").document(userUID).collection("locations").document(refLoc.id)
                refUser.set(dataUser).addOnSuccessListener {
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "New location added",
                            Toast.LENGTH_LONG
                        )
                    //refresh locations
                    getLocationHistory()
                    toast.show()
                }.addOnFailureListener {
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "Error adding a location",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                }
            }.addOnFailureListener {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "Error adding a location",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            }
    }

    // Search bar configuration
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode) {
            MaterialSearchBar.BUTTON_NAVIGATION -> drawer.openDrawer(GravityCompat.START)
            MaterialSearchBar.BUTTON_BACK -> searchBar.disableSearch()
        }
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        drawer.closeDrawer(GravityCompat.START)
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId

         if (id == R.id.nav_discussions) {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_evenements) {
//             val intent = Intent(this, MainActivity::class.java)
//             startActivity(intent)
        } else if (id == R.id.nav_profil) {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_rechercher) {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}


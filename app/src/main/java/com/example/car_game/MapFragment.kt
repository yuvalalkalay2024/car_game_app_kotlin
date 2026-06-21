package com.example.car_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: ScoreViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // טעינת המפה מתוך ה-XML פנימית
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // מיקום ברירת מחדל ראשוני (מרכז הארץ)
        val defaultLocation = LatLng(32.0853, 34.7818)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 8f))

        // האזנה לשינויים ב-ViewModel המשותף
        viewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            val targetLatLng = LatLng(location.first, location.second)

            googleMap?.clear() // מנקה סמנים קודמים
            googleMap?.addMarker(MarkerOptions().position(targetLatLng).title("High Score Location"))
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 14f)) // זום אין על המיקום
        }
    }
}
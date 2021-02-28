package com.example.googlemaps

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {// harita hazır olduğunda çalışan fonksiyon
        mMap = googleMap

        // Latitude -> Enlem
        // Longitude -> Boylam
        //41.011720, 28.983080
        /*
        val istanbul = LatLng(41.010098, 28.965225)
        mMap.addMarker(MarkerOptions().position(istanbul).title("Kapalı çarşı"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul, 15f))
*/

        // casting -> as
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //Lokasyon yada konum değişince yapılacak işler

                /*  println(location.latitude)
                  println(location.longitude) */

                val guncelKonum = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum, 15f))


                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adresListesi = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (adresListesi.size > 0) {
                        println(adresListesi.get(0).toString())

                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }

        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            //İZİN VERİLMEMİŞ
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {

            //İzin Zaten verilmiş

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)

            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null) {
                val sonBilinenLatlng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLatlng).title("Son bilinen konumunuz"))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatlng, 1f))

            }


        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1) {
            if (grantResults.size > 0) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                //İzin verildi.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = object : GoogleMap.OnMapLongClickListener {

        override fun onMapLongClick(location: LatLng?) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            if (location != null) {
                var adres = ""
                try {
                    val adresListesi = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (adresListesi.size > 0) {
                        if (adresListesi.get(0).thoroughfare != null) {
                            adres += adresListesi.get(0).thoroughfare

                            if (adresListesi.get(0).subThoroughfare != null) {
                                adres += adresListesi.get(0).subThoroughfare
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
                mMap.addMarker(MarkerOptions().position(location).title(adres))
            }
        }
    }
}
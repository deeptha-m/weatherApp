package com.example.njweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    //weather url to get JSON
    var weather_url1 = ""
    //api id for url
    var api_id1 = "d47f168b2bc6442699fec16296523482"
    private lateinit var textView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //link the textView in which the temperature will be displayed
        textView = findViewById(R.id.textView)
        //create an instance of the Fused Location Provider Client

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

        {when(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
        {

                PackageManager.PERMISSION_GRANTED -> {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    Log.e("lat", weather_url1)
                    btVar1.setOnClickListener {
                        Log.e("lat", "onClick")
                        //function to find the coordinates of the last location
                        obtainLocation()
                    }
                }
                PackageManager.PERMISSION_DENIED -> {

                }

        }
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
               /* fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                Log.e("lat", weather_url1)
                //on clicking this button function to get the coordinates will be called
                btVar1.setOnClickListener {
                    Log.e("lat", "onClick")
                    //function to find the coordinates of the last location
                    obtainLocation()
                }*/
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun obtainLocation(){
        Log.e("lat", "function")
        //get the last location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                //get the latitude and longitude and create the http URL
                weather_url1 = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ api_id1
                Log.e("lat", weather_url1.toString())
                //this function will fetch data from URL
                getTemp()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun getTemp() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url1
        Log.e("lat", url)
        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.e("lat", response.toString())
                //get the JSON object
                val obj = JSONObject(response)
                //get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.e("lat obj1", arr.toString())
                //get the JSON object from the array at index position 0
                val obj2 = arr.getJSONObject(0)
                Log.e("lat obj2", obj2.toString())
                //set the temperature and the city name using getString() function
                textView.text = obj2.getString("temp")+" deg Celsius in "+obj2.getString("city_name")
            },
            //In case of any error
            Response.ErrorListener { textView!!.text = "That didn't work!" })
        queue.add(stringReq)
    }
}

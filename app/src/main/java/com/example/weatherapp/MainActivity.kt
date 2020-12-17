package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    var cityList: MutableList<String> = mutableListOf("Helsinki", "Berlin", "Rome", "Cairo", "Nairobi")

    var API_LINK: String = ""
    var API_ICON: String = ""
    var API_KEY: String = ""

    companion object {
        var forecasts: MutableList<Forecast> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        API_LINK = "https://api.openweathermap.org/data/2.5/weather?q="
        API_ICON = "https://openweathermap.org/img/w/"
        API_KEY = getString(R.string.api_key)

        loadData()
    }

    private fun setUI() {
        // hide progress bar
        progressBar.visibility = View.INVISIBLE
        // add adapter
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        // add fab
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "How can you add and save a new city?", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadData() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        for (city in cityList) {
            val url = API_LINK + city + "&appid=" + API_KEY + "&units=metric"

            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    val mainJSONObject = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val firstWeatherObject = weatherArray.getJSONObject(0)

                    val city = response.getString("name")
                    val condition = firstWeatherObject.getString("main")
                    val temperature = mainJSONObject.getString("temp") + " °C"

                    val weatherTime: String = response.getString("dt")
                    val weatherLong: Long = weatherTime.toLong()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")
                    val dt = Instant.ofEpochSecond(weatherLong).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter).toString()

                    val icon = firstWeatherObject["icon"]
                    val iconUrl = API_ICON + icon + ".png"

                    forecasts.add(Forecast(city, condition, temperature, dt, iconUrl))

                    // Testing
                    Log.d("lastCity", forecasts.last().city + " " + forecasts.last().temperature)
                    setUI()
                },
                Response.ErrorListener { Log.d("errorMsg", "Something went wrong") })

            // Add the request to the RequestQueue.
            queue.add(request)
        }
    }
}
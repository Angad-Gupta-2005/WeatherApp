package com.angad.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//  2a7efbf7ebf1d34561d627af232a46ef

class MainActivity : AppCompatActivity() {
    //    Creating an instance of binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        Initialised the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        Function that fetch weather data
        fetchWeatherData("Mumbai")

//        Search city functionality
        searchCity()
    }

    //    Function that perform functionality for searching the weather condition of a city
    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
//        Setting the retrofit
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "2a7efbf7ebf1d34561d627af232a46ef", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(p0: Call<WeatherApp>, p1: Response<WeatherApp>) {
                val responseBody = p1.body()
                if (p1.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.sea_level
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    //    Log.d("TAG", "onResponse: ${temperature} ")
                    binding.temp.text = "$temperature ℃"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp ℃"
                    binding.minTemp.text = "Min Temp: $minTemp ℃"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.condition.text = condition
                    binding.sunRise.text = getTime(sunRise)
                    binding.sunset.text = getTime(sunSet)
                    binding.sea.text = "$seaLevel hPa"
                    binding.cityName.text = "$cityName "
                    binding.day.text = dayName()
                    binding.date.text = dateName()

//                    calling function that change background image according to the weather condition
                    changeBackgroundImage(condition)
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Please check your internet connection or Enter correct city name",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

//    Function that perform functionality to change the background image according to weather conditions
    private fun changeBackgroundImage(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear", "Smoke" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Haze", "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
    }

    //    Function that fetch date name from the system
    private fun dateName(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    //      Function that fetch day name from the system
    private fun dayName(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    //    Function that fetch time for sunset and sunrise
    private fun getTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH : mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}
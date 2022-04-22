package io.github.tuguzt.musicclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.tuguzt.musicclient.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private const val HEROKU_URL = "https://mdev-kotlin-crud.herokuapp.com"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(HEROKU_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: MusicController = retrofit.create(MusicController::class.java)

        binding.create.setOnClickListener {
            val id = when {
                binding.id.text.toString().isBlank() -> 0
                else -> binding.id.text.toString().toInt()
            }
            val name = binding.name.text.toString()
            val album = binding.album.text.toString()
            val callback = callback<Int> {
                binding.result.text = it.toString()
            }
            service.create(MusicEntry(id, name, album)).enqueue(callback)
        }

        binding.readEntry.setOnClickListener {
            val id = when {
                binding.id.text.toString().isBlank() -> 0
                else -> binding.id.text.toString().toInt()
            }
            val callback = callback<MusicEntry> {
                println(it)
                binding.result.text = it?.toString() ?: "There isn't music with such ID!"
            }
            service.readEntry(id).enqueue(callback)
        }

        binding.update.setOnClickListener {
            val id = when {
                binding.id.text.toString().isBlank() -> 0
                else -> binding.id.text.toString().toInt()
            }
            val name = binding.name.text.toString()
            val album = binding.album.text.toString()
            val callback = callback<Boolean> {
                binding.result.text = it.toString()
            }
            service.update(MusicEntry(id, name, album)).enqueue(callback)
        }

        binding.delete.setOnClickListener {
            val id = when {
                binding.id.text.toString().isBlank() -> 0
                else -> binding.id.text.toString().toInt()
            }
            val callback = callback<Boolean> {
                binding.result.text = it.toString()
            }
            service.delete(id).enqueue(callback)
        }

        binding.read.setOnClickListener {
            val callback = callback<List<MusicEntry>> { list ->
                binding.result.text = when {
                    list?.isNotEmpty() == true -> list.joinToString(separator = "\n") { "$it" }
                    else -> "List of music is empty!"
                }
            }
            service.read().enqueue(callback)
        }
    }

    private fun <T> callback(function: (response: T?) -> Unit) = object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = function(null)

        override fun onResponse(call: Call<T>, response: Response<T>) = function(response.body())
    }
}

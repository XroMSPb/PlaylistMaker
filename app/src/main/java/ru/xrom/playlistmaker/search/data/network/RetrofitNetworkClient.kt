package ru.xrom.playlistmaker.search.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.xrom.playlistmaker.search.data.NetworkClient
import ru.xrom.playlistmaker.search.data.dto.Response
import ru.xrom.playlistmaker.search.data.dto.TrackRequest
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com/"
    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api = client.create(ItunesApi::class.java)


    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackRequest) {
                val response = api.search(dto.expression).execute()
                val body = response.body() ?: Response()
                return body.apply { resultCode = response.code() }
            } else {
                return Response().apply { resultCode = 400 }
            }
        } catch (error: IOException) {
            Response().apply { resultCode = 500 }
        }
    }
}
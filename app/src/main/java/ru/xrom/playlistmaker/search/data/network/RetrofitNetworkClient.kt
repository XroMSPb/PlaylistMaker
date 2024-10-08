package ru.xrom.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.xrom.playlistmaker.search.data.NetworkClient
import ru.xrom.playlistmaker.search.data.dto.Response
import ru.xrom.playlistmaker.search.data.dto.TrackRequest

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com/"
    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api = client.create(ItunesApi::class.java)


    override suspend fun doRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            try {
                if (dto is TrackRequest) {
                    val response = api.search(dto.expression)
                    return@withContext response.apply { resultCode = 200 }
                } else {
                    return@withContext Response().apply { resultCode = 400 }
                }
            } catch (error: Throwable) {
                return@withContext Response().apply { resultCode = 500 }
            }
        }
    }
}
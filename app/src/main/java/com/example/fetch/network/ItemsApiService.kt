package com.example.fetch.network

import com.example.fetch.model.Item
import retrofit2.http.GET
import retrofit2.Response

interface ItemsApiService {

    @GET("hiring.json")
    suspend fun fetchItems(): Response<List<Item>>
}
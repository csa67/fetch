package com.example.fetch.repository

import com.example.fetch.model.Item
import com.example.fetch.network.ItemsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ItemRepository @Inject constructor(private val itemsApiService: ItemsApiService) {

    suspend fun getItems(): Response<List<Item>> = withContext(Dispatchers.IO){
        itemsApiService.fetchItems()
    }
}
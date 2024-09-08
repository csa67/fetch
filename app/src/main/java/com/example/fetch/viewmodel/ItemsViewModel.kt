package com.example.fetch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetch.model.Item
import com.example.fetch.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(private val itemRepository: ItemRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    init {
        fetchItems()
    }

    private fun fetchItems(){
        viewModelScope.launch {
            val response = itemRepository.getItems()
            if(response.isSuccessful){
                response.body()?.let { itemsList ->
                    _items.value = itemsList.filter { item -> item.name?.isNotEmpty() == true }
                        .sortedWith(compareBy<Item> {it.listId}.thenBy { it.name })
                }
            }
        }
    }
}
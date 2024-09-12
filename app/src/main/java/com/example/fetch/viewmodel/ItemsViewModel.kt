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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch {
            try {
                val response = itemRepository.getItems()
                if (response.isSuccessful) {
                    response.body()?.let { itemsList ->
                        _items.value = itemsList.filter { item -> item.name?.isNotEmpty() == true }
                    }
                } else {
                    setErrorMessage("Error: ${response.code()} - ${response.message() ?: "Unknown error"}")
                }
            } catch (e: Exception) {
                setErrorMessage("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}

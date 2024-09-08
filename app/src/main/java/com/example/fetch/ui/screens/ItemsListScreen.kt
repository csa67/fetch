package com.example.fetch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fetch.model.Item
import com.example.fetch.viewmodel.ItemsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ItemsListScreen(viewModel: ItemsViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState(emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState("")

    // Properly sort and group items by listId
    val groupedItems = items.groupBy { it.listId }
        .mapValues { entry ->
            entry.value.filter { it.name != null }
                .sortedWith(compareBy<Item> { it.name.orEmpty() }.thenBy { it.id })
        }


    val expandedStateMap = remember { mutableStateMapOf<Int, Boolean>() }

    if(items.isEmpty() || errorMessage?.isNotEmpty() == true){
        Text("No items available",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium)
    }else {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            groupedItems.forEach { (listId, itemList) ->
                item {
                    ListIdHeader(listId, expandedStateMap)
                }
                if (expandedStateMap[listId] == true) {
                    items(itemList) { item ->
                        ListItem(item = item)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ListIdHeader(listId: Int, expandedStateMap: MutableMap<Int, Boolean>) {
    val isExpanded = expandedStateMap[listId] ?: false  // Default to collapsed

    Text(
        text = "List $listId",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedStateMap[listId] = !isExpanded }
            .padding(12.dp)
    )
}

@Composable
fun ListItem(item: Item) {
    Text("${item.name}", modifier = Modifier.padding(12.dp))
}

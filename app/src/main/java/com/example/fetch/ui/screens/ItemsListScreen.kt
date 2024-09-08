package com.example.fetch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fetch.R
import com.example.fetch.model.Item
import com.example.fetch.viewmodel.ItemsViewModel

@Composable
fun ItemsListScreen(viewModel: ItemsViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState(emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState("")

    // Properly sort and group items by listId
    val groupedItems = items.groupBy { it.listId }
        .toSortedMap()
        .mapValues { entry ->
            entry.value.filter { it.name != null }
                .sortedWith(compareBy<Item> { it.name.orEmpty() }.thenBy { it.id })
        }

    val expandedStateMap = remember { mutableStateMapOf<Int, Boolean>() }

    if (items.isEmpty() || errorMessage?.isNotEmpty() == true) {
        Text(
            text = "No items available",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium
        )
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Items List", style = MaterialTheme.typography.titleLarge)

            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                groupedItems.forEach { (listId, itemList) ->
                    // Each group wrapped in a card
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Column {
                                ListIdHeader(listId, expandedStateMap[listId] ?: false) {
                                    expandedStateMap[listId] =
                                        !expandedStateMap.getOrDefault(listId, false)
                                }

                                if (expandedStateMap[listId] == true) {
                                    itemList.forEach { item ->
                                        ListItem(item = item)
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListIdHeader(listId: Int, isExpanded: Boolean, onHeaderClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "List $listId",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )

        Icon(
            painter = if (isExpanded) painterResource(id = R.drawable.ic_arrow_up) else painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = if (isExpanded) "Collapse" else "Expand"
        )
    }
}

@Composable
fun ListItem(item: Item) {
    Text(
        text = item.name ?: "Unknown",
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}

package com.example.fetch.repository

import com.example.fetch.model.Item
import com.example.fetch.network.ItemsApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class ItemRepositoryTest {

    @Mock
    lateinit var itemsApiService: ItemsApiService

    private lateinit var itemRepository: ItemRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        itemRepository = ItemRepository(itemsApiService)
    }

    @Test
    fun `getItems should return successful response`() = runTest {
        // Arrange: Mock a successful response
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item 1"),
            Item(id = 2, listId = 2, name = "Item 2")
        )
        `when`(itemsApiService.fetchItems()).thenReturn(Response.success(mockItems))

        // Act: Call the repository method
        val response = itemRepository.getItems()

        // Assert: Verify the result
        assertEquals(true, response.isSuccessful)
        assertEquals(2, response.body()?.size)
        assertEquals("Item 1", response.body()?.get(0)?.name)
    }

    @Test
    fun `getItems should return error response`() = runTest {
        // Arrange: Mock an error response (HTTP 404)
        val errorResponse = Response.error<List<Item>>(404, ResponseBody.create(null, "Not Found"))
        `when`(itemsApiService.fetchItems()).thenReturn(errorResponse)

        // Act: Call the repository method
        val response = itemRepository.getItems()

        // Assert: Verify the result
        assertEquals(false, response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun `getItems should throw exception when network fails`() = runTest {
        // Arrange: Mock a network failure (throw an exception)
        `when`(itemsApiService.fetchItems()).thenThrow(RuntimeException("Network error"))

        try {
            // Act: Call the repository method
            itemRepository.getItems()
            assert(false) // Force fail if exception isn't thrown
        } catch (e: Exception) {
            // Assert: Verify that the exception is the expected one
            assertEquals("Network error", e.message)
        }
    }
}

package com.example.fetch.viewmodel

import android.text.style.LineBackgroundSpan.Standard
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fetch.model.Item
import com.example.fetch.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class ItemsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    // Rule for LiveData/StateFlow testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var itemRepository: ItemRepository

    private lateinit var viewModel: ItemsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ItemsViewModel(itemRepository)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchItems should update items when repository returns successful response`() = runTest {
        // Arrange: Mock a successful response from the repository
        val mockItems = listOf(
            Item(id = 1, listId = 1, name = "Item 1"),
            Item(id = 2, listId = 2, name = "Item 2")
        )
        `when`(itemRepository.getItems()).thenReturn(Response.success(mockItems))

        // Act: Trigger fetchItems() in the ViewModel
        viewModel.fetchItems()

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Check that items in ViewModel are updated correctly
        val result = viewModel.items.first()
        assertEquals(2, result.size)
        assertEquals("Item 1", result[0].name)
    }

    @Test
    fun `fetchItems should update errorMessage when repository returns unsuccessful response`() = runTest {
        // Arrange: Mock an unsuccessful response (HTTP 500 error) from the repository
        val errorResponse = Response.error<List<Item>>(500, okhttp3.ResponseBody.create(null, "Internal Server Error"))

        // Use the correct stubbing with `thenReturn`
        `when`(itemRepository.getItems()).thenReturn(errorResponse)

        // Act: Trigger fetchItems() in the ViewModel
        viewModel.fetchItems()

        // Advance coroutines until all work is done
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Check that errorMessage is updated with the correct error message
        assertNotEquals(viewModel.errorMessage.first(),"")
    }


    @Test
    fun `fetchItems should update errorMessage when exception occurs`() = runTest {
        // Arrange: Mock an exception from the repository
        `when`(itemRepository.getItems()).thenThrow(RuntimeException("Network error"))

        // Act: Trigger fetchItems() in the ViewModel
        viewModel.fetchItems()

        // Assert: Check that errorMessage is updated with the exception message
        assertNotNull(viewModel.errorMessage.first())
    }

    @Test
    fun `clearError should reset errorMessage to null`() = runTest {
        // Arrange: Set an initial error message
        viewModel._errorMessage.value = "Error message"

        // Act: Call clearError() in the ViewModel
        viewModel.clearError()

        // Assert: Check that errorMessage is cleared
        val error = viewModel.errorMessage.first()
        assertNull(error)
    }
}

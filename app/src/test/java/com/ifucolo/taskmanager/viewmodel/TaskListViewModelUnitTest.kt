package com.ifucolo.taskmanager.viewmodel

import com.ifucolo.taskmanager.TaskStubFactory
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.viewmodel.generics.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class TaskListViewModelUnitTest {

    private lateinit var viewModel: TaskListViewModel
    private val taskRepository: TaskRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TaskListViewModel(taskRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetch tasks successfully`() = runTest {
        val sampleTasks = TaskStubFactory.createTaskList(1)
        val taskFlow: Flow<List<TaskWithCategory>> = flow { emit(sampleTasks) }

        whenever(taskRepository.allTasks).thenReturn(taskFlow)

        viewModel.fetchTasks()
        advanceUntilIdle()

        assertEquals(ViewState.Data(sampleTasks), viewModel.tasksViewState.value)
    }

    @Test
    fun `fetch tasks error`() = runTest {
        whenever(taskRepository.allTasks).thenThrow(RuntimeException("Failed to load tasks"))

        viewModel.fetchTasks()

        assertTrue(viewModel.tasksViewState.value is ViewState.Error)
    }

    @Test
    fun `delete task successfully`() = runTest {
        val sampleTasks = TaskStubFactory.createTaskList(1)
        val taskFlow = MutableStateFlow(sampleTasks)

        whenever(taskRepository.allTasks).thenReturn(taskFlow)
        whenever(taskRepository.delete(any())).thenAnswer {
            taskFlow.value = listOf()
            Unit
        }

        viewModel.fetchTasks()
        advanceUntilIdle()

        assertEquals(ViewState.Data(sampleTasks), viewModel.tasksViewState.value)

        val taskToDelete = sampleTasks.first().task
        viewModel.deleteTask(taskToDelete)
        advanceUntilIdle()

        assertEquals(ViewState.Data(emptyList<TaskWithCategory>()), viewModel.tasksViewState.value)
    }
}

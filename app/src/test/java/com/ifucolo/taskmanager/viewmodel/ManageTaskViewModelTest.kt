package com.ifucolo.taskmanager.viewmodel

import com.ifucolo.taskmanager.TaskStubFactory
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.data.repository.CategoryRepository
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.domain.usecase.SaveTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ManageTaskViewModelTest {
    private lateinit var viewModel: ManageTaskViewModel
    private val taskRepository: TaskRepository = mock()
    private val categoryRepository: CategoryRepository = mock()
    private val saveTaskUseCase: SaveTaskUseCase = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            ManageTaskViewModel(taskRepository, categoryRepository, saveTaskUseCase, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load categories on init`() = runTest {
        val categories = TaskStubFactory.createCategoryList()
        val categoriesFlow = MutableStateFlow(categories)
        
        whenever(categoryRepository.allCategories).thenReturn(categoriesFlow)

        viewModel.fetchCategories()
        advanceUntilIdle()

        val result = viewModel.categories.first()
        assertEquals(categories, result)
    }

    @Test
    fun `should set task title`() = runTest {
        // When
        viewModel.setTaskTitle("New Task Title")

        // Then
        val result = viewModel.taskTitle.first()
        assertEquals("New Task Title", result)
    }

    @Test
    fun `should set task description`() = runTest {
        // When
        viewModel.setTaskDescription("New Task Description")

        // Then
        val result = viewModel.taskDescription.first()
        assertEquals("New Task Description", result)
    }

    @Test
    fun `should set task category id`() = runTest {
        // When
        viewModel.setTaskCategoryId(2)

        // Then
        val result = viewModel.taskCategoryId.first()
        assertEquals(2, result)
    }

    @Test
    fun `should find task by id`() = runTest {
        // Given
        val taskWithCategory = TaskWithCategory(
            task = TaskStubFactory.createTask(
                id = 2,
                title = "Sample Task",
                description = "Sample Description"
            ).task,
            category = null
        )
        whenever(taskRepository.queryTask(1)).thenReturn(taskWithCategory)

        // When
        viewModel.findTaskById(1)
        advanceUntilIdle()

        assertEquals("Sample Task", viewModel.taskTitle.first())
        assertEquals("Sample Description", viewModel.taskDescription.first())
        assertEquals(2, viewModel.taskCategoryId.first())
    }

    @Test
    fun `should save task when handleSaveTask is called`() = runTest {
        // Given
        viewModel.setTaskTitle("Task Title")
        viewModel.setTaskDescription("Task Description")
        viewModel.setTaskCategoryId(1)

        // When
        viewModel.handleSaveTask(taskId = null, isEdit = false)
        advanceUntilIdle()

        // Then
        verify(saveTaskUseCase).execute(
            taskId = isNull(),
            taskTitle = eq("Task Title"),
            taskDescription = eq("Task Description"),
            taskCategoryId = eq(1),
            isEdit = eq(false)
        )
    }

    @Test
    fun `should add category when addCategory is called`() = runTest {
        // Given
        val category = TaskStubFactory.createCategory()
        whenever(categoryRepository.insert(category)).thenReturn(1L)
        var addedCategoryId: Int? = null

        // When
        viewModel.addCategory(category) { addedCategoryId = it }
        advanceUntilIdle()

        // Then
        assertEquals(1, addedCategoryId)
        verify(categoryRepository).insert(category)
    }

}
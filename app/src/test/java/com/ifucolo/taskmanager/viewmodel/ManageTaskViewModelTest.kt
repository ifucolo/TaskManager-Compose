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
    private val categories = TaskStubFactory.createCategoryList()
    private val categoriesFlow = MutableStateFlow(categories)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            ManageTaskViewModel(taskRepository, categoryRepository, saveTaskUseCase, testDispatcher)
        whenever(categoryRepository.allCategories).thenReturn(categoriesFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load categories on init`() = runTest {
        whenever(categoryRepository.allCategories).thenReturn(categoriesFlow)

        viewModel.fetchCategories()
        advanceUntilIdle()

        val result = viewModel.categories.first()
        assertEquals(categories, result)
    }

    @Test
    fun `should set task title`() = runTest {
        viewModel.setTaskTitle("New Task Title")

        val result = viewModel.taskTitle.first()
        assertEquals("New Task Title", result)
    }

    @Test
    fun `should set task description`() = runTest {
        viewModel.setTaskDescription("New Task Description")

        val result = viewModel.taskDescription.first()
        assertEquals("New Task Description", result)
    }

    @Test
    fun `should set task category id`() = runTest {
        viewModel.setTaskCategoryId(2)

        val result = viewModel.taskCategoryId.first()
        assertEquals(2, result)
    }

    @Test
    fun `should find task by id`() = runTest {
        val taskWithCategory = TaskWithCategory(
            task = TaskStubFactory.createTask(
                id = 2,
                title = "Sample Task",
                description = "Sample Description"
            ).task,
            category = null
        )
        whenever(taskRepository.queryTask(1)).thenReturn(taskWithCategory)

        viewModel.findTaskById(1)
        advanceUntilIdle()

        assertEquals("Sample Task", viewModel.taskTitle.first())
        assertEquals("Sample Description", viewModel.taskDescription.first())
    }

    @Test
    fun `should save task when handleSaveTask is called`() = runTest {
        viewModel.setTaskTitle("Task Title")
        viewModel.setTaskDescription("Task Description")
        viewModel.setTaskCategoryId(1)

        viewModel.handleSaveTask(taskId = null, isEdit = false)
        advanceUntilIdle()

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
        val category = TaskStubFactory.createCategory()
        whenever(categoryRepository.insert(category)).thenReturn(1L)
        var addedCategoryId: Int? = null

        viewModel.addCategory(category) { addedCategoryId = it }
        advanceUntilIdle()

        assertEquals(1, addedCategoryId)
        verify(categoryRepository).insert(category)
    }

}
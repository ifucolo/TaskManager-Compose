package com.ifucolo.taskmanager.repository

import com.ifucolo.taskmanager.TaskStubFactory
import com.ifucolo.taskmanager.data.local.dao.TaskDao
import com.ifucolo.taskmanager.data.local.entities.Task
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.data.repository.TaskRepositoryImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class TaskRepositoryImplUnitTest {
    private lateinit var taskDao: TaskDao
    private lateinit var taskRepository: TaskRepository

    @Before
    fun setUp() {
        taskDao = mock()
        taskRepository = TaskRepositoryImpl(taskDao)
        whenever(taskDao.getAllTasks()).thenReturn(flowOf(TaskStubFactory.createTaskList()))
    }

    @Test
    fun `direct DAO flow collection`() = runTest {
        val taskFlow = taskDao.getAllTasks()
        assertNotNull(taskFlow)

        val daoResult = taskFlow.toList()
        assertEquals(1, daoResult.size)
        assertEquals("Sample Task", daoResult.first()[0].task.title)
    }

    @Test
    fun `getAllTasks returns flow of tasks`() = runTest {
        val expectedTasks = TaskStubFactory.createTaskList()
        val taskFlow: Flow<List<TaskWithCategory>> = flowOf(expectedTasks)

        whenever(taskDao.getAllTasks()).thenReturn(taskFlow)
        val result = taskDao.getAllTasks().toList()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Sample Task", result.first()[0].task.title)
    }

    @Test
    fun `queryTask calls DAO and returns expected task`() = runTest {
        val taskId = 0
        val expectedTask = TaskStubFactory.createTask()
        whenever(taskDao.getTaskWithCategory(taskId)).thenReturn(expectedTask)

        val result = taskRepository.queryTask(taskId)

        assert(result == expectedTask)
        verify(taskDao).getTaskWithCategory(taskId)
    }

    @Test
    fun `insert calls DAO with correct task`() = runTest {
        val task = TaskStubFactory.createTask().task
        taskRepository.insert(task)

        verify(taskDao).insert(task)
    }

    @Test
    fun `delete calls DAO with correct task`() = runTest {
        val task = TaskStubFactory.createTask().task
        taskRepository.delete(task)

        verify(taskDao).delete(task)
    }

    @Test
    fun `update calls DAO with correct task`() = runTest {
        val task = TaskStubFactory.createTask().task
        taskRepository.update(task)

        verify(taskDao).update(task)
    }

    @Test
    fun `queryTask throws exception when DAO fails`() = runTest {
        val taskId = 1
        whenever(taskDao.getTaskWithCategory(taskId)).thenThrow(RuntimeException("Database error"))

        try {
            taskRepository.queryTask(taskId)
            fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            assertTrue(e is RuntimeException)
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun `delete task and check if task is removed from allTasks`() = runTest {
        val initialTasks = TaskStubFactory.createTaskList().toMutableList()
        val taskFlow = MutableStateFlow<List<TaskWithCategory>>(initialTasks)

        whenever(taskDao.getAllTasks()).thenReturn(taskFlow)
        whenever(taskDao.delete(any())).thenAnswer { mock ->
            val taskId = mock.getArgument<Task>(0).id

            initialTasks.removeIf { it.task.id == taskId }
            taskFlow.value = initialTasks.toList()
            Unit
        }

        val taskToBeDeleted = initialTasks[0].task
        taskRepository.delete(taskToBeDeleted)
        advanceUntilIdle()

        val result = taskDao.getAllTasks().first()

        assertEquals(4, result.size)
        assertEquals(1, result.first().task.id)
    }
}
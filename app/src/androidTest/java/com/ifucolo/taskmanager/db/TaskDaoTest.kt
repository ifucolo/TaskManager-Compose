package com.ifucolo.taskmanager.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifucolo.taskmanager.data.local.dao.CategoryDao
import com.ifucolo.taskmanager.data.local.dao.TaskDao
import com.ifucolo.taskmanager.data.local.database.TaskDatabase
import com.ifucolo.taskmanager.util.TaskStubFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = database.taskDao()
        categoryDao = database.categoryDao()
    }

    @After
    fun teardown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertAndGetTask() = runBlocking {
        // Arrange
        val task = TaskStubFactory.createTask(id = 1).task

        taskDao.insert(task)
        delay(1000) // 100 milliseconds might be enough to ensure data is written

        val taskWithCategory = taskDao.getTaskWithCategory(1)

        assertEquals(task.id, taskWithCategory.task?.id)
        assertEquals(task.title, taskWithCategory.task?.title)
    }

    @Test
    fun getAllTasks() = runBlocking {
        val task1 = TaskStubFactory.createTask(id = 1).task
        val task2 = TaskStubFactory.createTask(id = 2).task
        taskDao.insert(task1)
        taskDao.insert(task2)

        val tasks = taskDao.getAllTasks().first()

        assertEquals(2, tasks.size)
        assertEquals(task1.title, tasks[0].task.title)
        assertEquals(task2.title, tasks[1].task.title)
    }

    @Test
    fun updateTask() = runBlocking {
        val task = TaskStubFactory.createTask(id = 1).task
        taskDao.insert(task)

        val updatedTask = task.copy(1, "Updated Task", "Updated Description", null, false)
        taskDao.update(updatedTask)

        val taskWithCategory = taskDao.getTaskWithCategory(task.id)

        assertEquals(updatedTask.title, taskWithCategory.task.title)
        assertEquals(updatedTask.description, taskWithCategory.task.description)
    }

    @Test
    fun deleteTask() = runBlocking {
        val task = TaskStubFactory.createTask(id = 1).task
        val task2 = TaskStubFactory.createTask(id = 2).task

        taskDao.insert(task)
        taskDao.delete(task)

        val tasks = taskDao.getAllTasks().first()
        assertEquals(0, tasks.size)

        taskDao.insert(task2)

        val tasks2 = taskDao.getAllTasks().first()
        assertEquals(1, tasks2.size)
    }

    @Test
    fun insertTaskWithSameId() = runBlocking {
        val task1 = TaskStubFactory.createTask(id = 1).task
        val task2 = task1.copy(title = "Duplicate ID Task") // Same ID as task1 but different title

        taskDao.insert(task1)
        taskDao.insert(task2) // This will replace task1 if REPLACE is the conflict strategy

        val tasks = taskDao.getAllTasks().first()

        assertEquals(
            1,
            tasks.size
        ) // Expecting 1 task if REPLACE or ABORT, otherwise IGNORE keeps the first task
        assertEquals("Duplicate ID Task", tasks[0].task.title) // Expect the second task if REPLACE
    }

    @Test
    fun insertTaskWithNullCategory() = runBlocking {
        val task = TaskStubFactory.createTask(id = 1).task

        taskDao.insert(task)
        val taskWithCategory = taskDao.getTaskWithCategory(task.id)

        assertEquals(null, taskWithCategory.task?.categoryId) // Expect category to be null
        assertEquals(task.title, taskWithCategory.task?.title)
    }

    @Test
    fun deleteNonExistentTask() = runBlocking {
        val task = TaskStubFactory.createTask(id = 1).task

        taskDao.delete(task) // Deleting a task that hasn't been inserted should not throw an error
        val tasks = taskDao.getAllTasks().first()

        assertEquals(0, tasks.size) // The list should still be empty
    }

    @Test
    fun updateNonExistentTask() = runBlocking {
        val task = TaskStubFactory.createTask(id = 1).task

        taskDao.update(task) // Trying to update a task that hasn't been inserted

        val tasks = taskDao.getAllTasks().first()
        assertEquals(0, tasks.size) // The list should still be empty
    }

    @Test
    fun getTasksByCategory() = runBlocking {
        val category = TaskStubFactory.createCategory()
        categoryDao.insert(category)

        val task1 = TaskStubFactory.createTask(id = 1, categoryId = category.id).task
        val task2 = TaskStubFactory.createTask(id = 2, categoryId = category.id).task

        taskDao.insert(task1)
        taskDao.insert(task2)

        val workTasks = taskDao.getTasksByCategory(1).first()
        assertEquals(2, workTasks.size)
        assertEquals(task1.categoryId, workTasks[0].task.categoryId)
    }

    @Test
    fun deleteAllTasks() = runBlocking {
        val task1 = TaskStubFactory.createTask(id = 1).task
        val task2 = TaskStubFactory.createTask(id = 2).task

        taskDao.insert(task1)
        taskDao.insert(task2)

        taskDao.deleteAllTasks() // Assume you have this method

        val tasks = taskDao.getAllTasks().first()

        assertEquals(0, tasks.size) // All tasks should be deleted
    }


}

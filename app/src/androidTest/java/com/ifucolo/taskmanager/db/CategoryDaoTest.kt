package com.ifucolo.taskmanager.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ifucolo.taskmanager.data.local.dao.CategoryDao
import com.ifucolo.taskmanager.data.local.database.TaskDatabase
import com.ifucolo.taskmanager.util.TaskStubFactory
import com.ifucolo.taskmanager.util.TaskStubFactory.RED_HEX
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()

        categoryDao = database.categoryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAllCategories() = runBlocking {
        val category = TaskStubFactory.createCategory(categoryName = "Work")

        categoryDao.insert(category)
        val categories = categoryDao.getAllCategories().first()

        assertEquals(1, categories.size)
        assertEquals(category.name, categories[0].name)
        assertEquals(category.color, categories[0].color)
    }

    @Test
    fun updateCategory() = runBlocking {
        // Arrange
        val category = TaskStubFactory.createCategory(categoryName = "Work")

        categoryDao.insert(category)

        val updatedCategory = category.copy(name = "Personal", color = RED_HEX)

        // Act
        categoryDao.update(updatedCategory)
        val categories = categoryDao.getAllCategories().first()

        // Assert
        assertEquals(1, categories.size)
        assertEquals("Personal", categories[0].name)
        assertEquals(RED_HEX, categories[0].color)
    }

    @Test
    fun deleteCategory() = runBlocking {
        val category = TaskStubFactory.createCategory()
        categoryDao.insert(category)

        categoryDao.delete(category)
        val categories = categoryDao.getAllCategories().first()

        assertEquals(0, categories.size) // Ensure category list is empty
    }

    @Test
    fun deleteAllTasks() = runBlocking {
        val category = TaskStubFactory.createCategory()
        val category2 = TaskStubFactory.createCategory(categoryId = 2)

        categoryDao.insert(category)
        categoryDao.insert(category2)

        categoryDao.deleteAllCategories() // Assume you have this method

        val tasks = categoryDao.getAllCategories().first()

        assertEquals(0, tasks.size) // All tasks should be deleted
    }

    @Test
    fun deleteNonExistentTask() = runBlocking {
        val category = TaskStubFactory.createCategory()

        categoryDao.delete(category) // Deleting a task that hasn't been inserted should not throw an error
        val tasks = categoryDao.getAllCategories().first()

        assertEquals(0, tasks.size) // The list should still be empty
    }

    @Test
    fun updateNonExistentTask() = runBlocking {
        val category = TaskStubFactory.createCategory()

        categoryDao.update(category) // Trying to update a task that hasn't been inserted

        val tasks = categoryDao.getAllCategories().first()
        assertEquals(0, tasks.size) // The list should still be empty
    }
}

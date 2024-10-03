package com.ifucolo.taskmanager.repository

import com.ifucolo.taskmanager.TaskStubFactory
import com.ifucolo.taskmanager.data.local.dao.CategoryDao
import com.ifucolo.taskmanager.data.local.entities.Category
import com.ifucolo.taskmanager.data.repository.CategoryRepository
import com.ifucolo.taskmanager.data.repository.CategoryRepositoryImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
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
class CategoryRepositoryUnitTest {
    private lateinit var categoryDao: CategoryDao
    private lateinit var categoryRepository: CategoryRepository

    @Before
    fun setUp() {
        categoryDao = mock()
        categoryRepository = CategoryRepositoryImpl(categoryDao)
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(TaskStubFactory.createCategoryList()))
    }

    @Test
    fun `direct DAO flow collection`() = runTest {
        val categoryFlow = categoryDao.getAllCategories()
        assertNotNull(categoryFlow)

        val daoResult = categoryFlow.toList()
        assertEquals(1, daoResult.size)
        assertEquals("Sample Category", daoResult.first()[0].name)
    }

    @Test
    fun `getAllCategories returns flow of categories`() = runTest {
        val expectedCategories = TaskStubFactory.createCategoryList()
        val categoryFlow: Flow<List<Category>> = flowOf(expectedCategories)

        whenever(categoryDao.getAllCategories()).thenReturn(categoryFlow)
        val result = categoryDao.getAllCategories().toList()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Sample Category", result.first()[0].name)
    }

    @Test
    fun `insert calls DAO with correct category`() = runTest {
        val category = TaskStubFactory.createCategory()
        categoryRepository.insert(category)

        verify(categoryDao).insert(category)
    }


    @Test
    fun `delete calls DAO with correct category`() = runTest {
        val category = TaskStubFactory.createCategory()
        categoryRepository.delete(category)

        verify(categoryDao).delete(category)
    }

    @Test
    fun `delete a category and check if category is removed from allCategories`() = runTest {
        val initialCategories = TaskStubFactory.createCategoryList().toMutableList()
        val categoryFlow = MutableStateFlow<List<Category>>(initialCategories)

        whenever(categoryDao.getAllCategories()).thenReturn(categoryFlow)
        whenever(categoryDao.delete(any())).thenAnswer { mock ->
            val categoryId = mock.getArgument<Category>(0).id

            initialCategories.removeIf { it.id == categoryId }
            categoryFlow.value = initialCategories.toList()
            Unit
        }

        val categoryToBeDeleted = initialCategories[0]
        categoryRepository.delete(categoryToBeDeleted)
        advanceUntilIdle()

        val result = categoryDao.getAllCategories().first()

        assertEquals(4, result.size)
        assertEquals(1, result.first().id)
    }

}
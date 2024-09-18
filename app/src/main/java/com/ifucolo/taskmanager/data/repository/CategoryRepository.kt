package com.ifucolo.taskmanager.data.repository

import com.ifucolo.taskmanager.data.local.dao.CategoryDao
import com.ifucolo.taskmanager.data.local.entities.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CategoryRepository {
    val allCategories: Flow<List<Category>>

    suspend fun insert(category: Category): Long
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
}

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) :
    CategoryRepository {
    override val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    override suspend fun insert(category: Category): Long = categoryDao.insert(category)
    override suspend fun update(category: Category) = categoryDao.update(category)
    override suspend fun delete(category: Category) = categoryDao.delete(category)
}
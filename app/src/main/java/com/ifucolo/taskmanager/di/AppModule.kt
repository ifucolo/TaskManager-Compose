package com.ifucolo.taskmanager.di

import android.content.Context
import com.ifucolo.taskmanager.data.local.dao.CategoryDao
import com.ifucolo.taskmanager.data.local.dao.TaskDao
import com.ifucolo.taskmanager.data.local.database.TaskDatabase
import com.ifucolo.taskmanager.data.repository.CategoryRepository
import com.ifucolo.taskmanager.data.repository.CategoryRepositoryImpl
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.data.repository.TaskRepositoryImpl
import com.ifucolo.taskmanager.domain.usecase.SaveTaskUseCase
import com.ifucolo.taskmanager.domain.usecase.SaveTaskUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext appContext: Context
    ): TaskDatabase {
        return TaskDatabase.createDataBase(appContext = appContext)
    }

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideCategoryDao(database: TaskDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    fun provideSaveTaskUseCase(taskRepository: TaskRepository): SaveTaskUseCase {
        return SaveTaskUseCaseImpl(taskRepository)
    }
}
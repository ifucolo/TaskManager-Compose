package com.ifucolo.taskmanager.usecase

import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.domain.usecase.SaveTaskUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class SaveTaskUseCaseImplTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var saveTaskUseCase: SaveTaskUseCaseImpl

    @Before
    fun setUp() {
        taskRepository = mock()
        saveTaskUseCase = SaveTaskUseCaseImpl(taskRepository)
    }

    @Test
    fun `should insert task when isEdit is false`() = runBlocking {
        val taskTitle = "New Task"
        val taskDescription = "Task description"
        val taskCategoryId: Int = 1

        saveTaskUseCase.execute(
            taskId = null,
            taskTitle = taskTitle,
            taskDescription = taskDescription,
            taskCategoryId = taskCategoryId,
            isEdit = false
        )


        verify(taskRepository).insert(any())
    }

    @Test
    fun `should update task when isEdit is true and taskId is provided`() = runBlocking {
        val taskId = 1
        val taskTitle = "Edited Task"
        val taskDescription = "Updated description"
        val taskCategoryId: Int = 1

        saveTaskUseCase.execute(
            taskId = taskId,
            taskTitle = taskTitle,
            taskDescription = taskDescription,
            taskCategoryId = taskCategoryId,
            isEdit = true
        )

        verify(taskRepository).update(any())
    }

    @Test
    fun `should not update task when isEdit is true but taskId is null`() = runBlocking {
        val taskTitle = "Edited Task"
        val taskDescription = "Updated description"
        val taskCategoryId: Int = 1

        saveTaskUseCase.execute(
            taskId = null,
            taskTitle = taskTitle,
            taskDescription = taskDescription,
            taskCategoryId = taskCategoryId,
            isEdit = true
        )

        verify(taskRepository, never()).update(any())
        verify(taskRepository, never()).insert(any())
    }

    @Test
    fun `should insert task when taskId is null and isEdit is false`() = runBlocking {
        val taskTitle = "New Task"
        val taskDescription = "Task description"
        val taskCategoryId: Int? = 1

        saveTaskUseCase.execute(
            taskId = null,
            taskTitle = taskTitle,
            taskDescription = taskDescription,
            taskCategoryId = taskCategoryId,
            isEdit = false
        )

        verify(taskRepository).insert(any())
    }
}

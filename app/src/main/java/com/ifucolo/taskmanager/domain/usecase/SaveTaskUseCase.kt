package com.ifucolo.taskmanager.domain.usecase

import com.ifucolo.taskmanager.data.local.entities.Task
import com.ifucolo.taskmanager.data.repository.TaskRepository
import javax.inject.Inject

interface SaveTaskUseCase {
    suspend fun execute(
        taskId: Int? = null,
        taskTitle: String,
        taskDescription: String,
        taskCategoryId: Int? = null,
        isEdit: Boolean
    )
}

class SaveTaskUseCaseImpl @Inject constructor(
    private val taskRepository: TaskRepository
) : SaveTaskUseCase {
    override suspend fun execute(
        taskId: Int?,
        taskTitle: String,
        taskDescription: String,
        taskCategoryId: Int?,
        isEdit: Boolean
    ) {
        val task = Task(
            title = taskTitle,
            description = taskDescription,
            categoryId = taskCategoryId
        )

        if (isEdit) {
            taskId?.let {
                val editTask = task.copy(id = it)
                taskRepository.update(editTask)
            }
        } else {
            taskRepository.insert(task)
        }
    }
}
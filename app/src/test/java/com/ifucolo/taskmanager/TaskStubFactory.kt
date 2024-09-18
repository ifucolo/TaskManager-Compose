package com.ifucolo.taskmanager

import androidx.compose.ui.graphics.Color
import com.ifucolo.taskmanager.data.local.entities.Category
import com.ifucolo.taskmanager.data.local.entities.Task
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.ui.colorToHex

object TaskStubFactory {

    fun createCategory(
        categoryId: Int = 1,
        categoryName: String = "Sample Category",
        color: Color = Color(0xFFD4E157)
    ): Category = Category(id = categoryId, name = categoryName, color = colorToHex(color))

    fun createCategoryList(
        size: Int = 5,
        startId: Int = 0
    ): List<Category> {
        return (startId until startId + size).map { id ->
            createCategory(categoryId = id)
        }
    }

    fun createTask(
        id: Int = 0,
        title: String = "Sample Task",
        description: String = "Sample Description",
        categoryId: Int? = null,
        categoryName: String = "Sample Category",
        color: Color = Color(0xFFD4E157)
    ): TaskWithCategory {
        val task = Task(id = id, title = title, description = description)
        val category =
            categoryId?.let { Category(id = it, name = categoryName, color = colorToHex(color)) }
        return TaskWithCategory(task, category)
    }

    fun createTaskList(
        size: Int = 5,
        startId: Int = 0
    ): List<TaskWithCategory> {
        return (startId until startId + size).map { id ->
            createTask(id = id)
        }
    }
}
package com.ifucolo.taskmanager.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"), // This is the column in the Category entity
        childColumns = arrayOf("categoryId"), // This is the column in the Task entity
        onDelete = ForeignKey.SET_NULL // Optional: delete tasks when the category is deleted
    )],
    indices = [Index(value = ["categoryId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var description: String,
    var categoryId: Int? = null,
    val isCompleted: Boolean = false
)
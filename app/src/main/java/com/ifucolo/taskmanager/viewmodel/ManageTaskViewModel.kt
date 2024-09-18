package com.ifucolo.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifucolo.taskmanager.data.local.entities.Category
import com.ifucolo.taskmanager.data.repository.CategoryRepository
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.domain.usecase.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    private val _taskTitle = MutableStateFlow("")
    val taskTitle: StateFlow<String> get() = _taskTitle

    private val _taskDescription = MutableStateFlow("")
    val taskDescription: StateFlow<String> get() = _taskDescription

    private val _taskCategoryId = MutableStateFlow<Int?>(null)
    val taskCategoryId: StateFlow<Int?> get() = _taskCategoryId

    init {
        fetchCategories()
    }

    open fun fetchCategories() {
        viewModelScope.launch(ioDispatcher) {
            categoryRepository.allCategories.collect { allCategories ->
                _categories.value = allCategories
            }
        }
    }

    fun setTaskTitle(title: String) {
        _taskTitle.value = title
    }

    fun setTaskDescription(description: String) {
        _taskDescription.value = description
    }

    fun setTaskCategoryId(categoryId: Int?) {
        _taskCategoryId.value = categoryId
    }

    fun findTaskById(taskId: Int) {
        viewModelScope.launch(ioDispatcher) {
            val task = taskRepository.queryTask(taskId)
            _taskTitle.value = task.task.title
            _taskDescription.value = task.task.description
            _taskCategoryId.value = task.task.categoryId
        }
    }

    fun handleSaveTask(taskId: Int?, isEdit: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            saveTaskUseCase.execute(
                taskId = taskId,
                taskTitle = _taskTitle.value,
                taskDescription = _taskDescription.value,
                taskCategoryId = _taskCategoryId.value,
                isEdit = isEdit
            )
        }
    }

    fun addCategory(category: Category, onAdded: (Int) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            val result = categoryRepository.insert(category)
            onAdded(result.toInt())
        }
    }
}
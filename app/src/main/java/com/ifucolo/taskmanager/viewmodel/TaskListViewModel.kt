package com.ifucolo.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifucolo.taskmanager.data.local.entities.Task
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.data.repository.TaskRepository
import com.ifucolo.taskmanager.viewmodel.generics.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _tasksViewState =
        MutableStateFlow<ViewState<List<TaskWithCategory>>>(ViewState.Loading)
    open val tasksViewState: StateFlow<ViewState<List<TaskWithCategory>>> get() = _tasksViewState

    init {
        fetchTasks()
    }

    open fun fetchTasks() {
        viewModelScope.launch(ioDispatcher) {
            _tasksViewState.value = ViewState.Loading
            try {
                taskRepository.allTasks.collect { taskList ->
                    _tasksViewState.value = ViewState.Data(data = taskList)
                }
            } catch (e: Exception) {
                println("Error during deletion: ${e.message}")
                _tasksViewState.value = ViewState.Error
            }
        }
    }

    open fun deleteTask(task: Task) {
        viewModelScope.launch(ioDispatcher) {
            _tasksViewState.value = ViewState.Loading
            try {
                taskRepository.delete(task)
                fetchTasks()
            } catch (e: Exception) {
                _tasksViewState.value = ViewState.Error
            }
        }
    }
}
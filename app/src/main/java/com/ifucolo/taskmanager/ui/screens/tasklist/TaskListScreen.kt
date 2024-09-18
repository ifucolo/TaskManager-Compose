package com.ifucolo.taskmanager.ui.screens.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifucolo.taskmanager.R
import com.ifucolo.taskmanager.data.local.entities.Task
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.ui.components.AppTopBar
import com.ifucolo.taskmanager.ui.components.ErrorScreen
import com.ifucolo.taskmanager.ui.components.LoadingIndicator
import com.ifucolo.taskmanager.ui.components.TaskItem
import com.ifucolo.taskmanager.ui.navigation.Screen
import com.ifucolo.taskmanager.viewmodel.TaskListViewModel
import com.ifucolo.taskmanager.viewmodel.generics.ViewState
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    navController: NavController
) {
    val taskViewState by viewModel.tasksViewState.collectAsState()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val snackBackBarMessage = stringResource(id = R.string.task_deleted)

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.task_list),
                backgroundColor = MaterialTheme.colorScheme.onBackground
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag(tag = ADD_TEST_TAG),
                onClick = {
                    navController.navigate(Screen.ADD_TASK.name)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_task))
            }
        },
        containerColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding ->

        when (taskViewState) {
            is ViewState.Data -> {
                TaskListContent(
                    tasks = (taskViewState as ViewState.Data<List<TaskWithCategory>>).data,
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.EDIT_TASK.name + "/$taskId")
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = snackBackBarMessage
                            )
                        }
                    },
                    innerPadding = innerPadding
                )
            }

            ViewState.Error -> {
                ErrorScreen()
            }

            ViewState.Loading -> {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun TaskListContent(
    tasks: List<TaskWithCategory>,
    onTaskClick: (Int) -> Unit,
    onDeleteTask: (Task) -> Unit,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskClick = { onTaskClick(task.task.id) },
                onDeleteTask = { onDeleteTask(task.task) }
            )
        }
    }
}

const val ADD_TEST_TAG = "ADD_TEST_TAG"


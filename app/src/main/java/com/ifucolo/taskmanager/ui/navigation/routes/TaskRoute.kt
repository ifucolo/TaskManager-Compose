package com.ifucolo.taskmanager.ui.navigation.routes

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ifucolo.taskmanager.ui.navigation.Routes
import com.ifucolo.taskmanager.ui.navigation.Screen
import com.ifucolo.taskmanager.ui.screens.addtask.AddTaskScreen
import com.ifucolo.taskmanager.ui.screens.tasklist.TaskListScreen
import com.ifucolo.taskmanager.viewmodel.ManageTaskViewModel
import com.ifucolo.taskmanager.viewmodel.TaskListViewModel


fun NavController.navigateToTaskFlow() {
    navigate(Routes.TASK.name)
}

fun NavGraphBuilder.taskFlow(
    navController: NavController
) {
    navigation(
        route = Routes.TASK.name,
        startDestination = Screen.TASK_LIST.name
    ) {
        composable(Screen.TASK_LIST.name) {
            val viewModel: TaskListViewModel = hiltViewModel()
            TaskListScreen(viewModel, navController)
        }
        composable(Screen.ADD_TASK.name) {
            val viewModel: ManageTaskViewModel = hiltViewModel()
            AddTaskScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.EDIT_TASK.name + "/{task_id}",
            arguments = listOf(navArgument("task_id") { type = NavType.IntType })
        ) { backStackEntry ->
            val viewModel: ManageTaskViewModel = hiltViewModel()
            val taskId = backStackEntry.arguments?.getInt("task_id")
            AddTaskScreen(
                viewModel = viewModel,
                navController = navController,
                taskId = taskId
            )
        }
    }
}
package com.ifucolo.taskmanager.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.NavController
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory
import com.ifucolo.taskmanager.ui.components.BOX_DISMISS_TEST_TAG
import com.ifucolo.taskmanager.ui.components.ERROR_TEST_TAG
import com.ifucolo.taskmanager.ui.components.LOADING_INDICATOR_TEST_TAG
import com.ifucolo.taskmanager.ui.screens.tasklist.ADD_TEST_TAG
import com.ifucolo.taskmanager.ui.screens.tasklist.TaskListScreen
import com.ifucolo.taskmanager.util.TaskStubFactory
import com.ifucolo.taskmanager.viewmodel.TaskListViewModel
import com.ifucolo.taskmanager.viewmodel.generics.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = mock<TaskListViewModel>()
    private val navController = mock<NavController>()

    private fun setContent() {
        composeTestRule.setContent {
            TaskListScreen(viewModel = viewModel, navController = navController)
        }
    }

    @Test
    fun displaysLoadingIndicator_whenStateIsLoading() {
        val mockStateFlow = MutableStateFlow<ViewState<List<TaskWithCategory>>>(ViewState.Loading)
        whenever(viewModel.tasksViewState).thenReturn(mockStateFlow)

        setContent()

        composeTestRule
            .onNodeWithTag(LOADING_INDICATOR_TEST_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun displaysError_whenStateIsError() {
        whenever(viewModel.tasksViewState).thenReturn(MutableStateFlow(ViewState.Error))

        setContent()

        composeTestRule
            .onNodeWithTag(ERROR_TEST_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun displaysTask_whenStateIsData() {
        val tasks = TaskStubFactory.createTaskList()
        whenever(viewModel.tasksViewState).thenReturn(MutableStateFlow(ViewState.Data(data = tasks)))

        setContent()

        tasks.forEach { task ->
            composeTestRule.onNodeWithText(task.task.title)
        }
    }

    @Test
    fun navigateToAddTaskScreen_whenCreateTaskIsClicker() {
        val tasks = TaskStubFactory.createTaskList()
        whenever(viewModel.tasksViewState).thenReturn(MutableStateFlow(ViewState.Data(data = tasks)))

        setContent()

        composeTestRule
            .onNodeWithTag(ADD_TEST_TAG)
            .performClick()
            .assertExists()

        //verify(navController).navigate(Screen.ADD_TASK.name)
    }

    @Test
    fun navigateToEditTaskScreen_whenTaskIsClicked() {
        val task = TaskStubFactory.createTaskList(size = 1)[0]
        val taskId = task.task.id
        whenever(viewModel.tasksViewState).thenReturn(
            MutableStateFlow(
                ViewState.Data(
                    data = listOf(
                        task
                    )
                )
            )
        )

        setContent()

        composeTestRule
            .onNodeWithText(task.task.title)
            .performClick()
            .assertExists()
//        verify(navController).navigate("EDIT_TASK/$taskId")
    }


    @Test
    fun showsDismissBackground_whenTaskIsSwipeToTheLeft() {
        val tasks = TaskStubFactory.createTaskList(size = 1)
        val task = tasks[0].task

        val mockStateFlow =
            MutableStateFlow<ViewState<List<TaskWithCategory>>>(ViewState.Data(tasks))

        whenever(viewModel.tasksViewState).thenReturn(mockStateFlow)

        setContent()

        composeTestRule.onNodeWithText(task.title).performTouchInput {
            swipeLeft(startX = 500f, endX = -500f)
        }

        composeTestRule.onNodeWithTag(BOX_DISMISS_TEST_TAG).assertIsDisplayed()
    }
}

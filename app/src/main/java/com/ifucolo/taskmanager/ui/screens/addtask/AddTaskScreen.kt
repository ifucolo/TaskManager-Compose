package com.ifucolo.taskmanager.ui.screens.addtask

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifucolo.taskmanager.R
import com.ifucolo.taskmanager.ui.ColorSaver
import com.ifucolo.taskmanager.ui.components.AppTopBar
import com.ifucolo.taskmanager.ui.components.CategorySelector
import com.ifucolo.taskmanager.ui.components.PrimaryButton
import com.ifucolo.taskmanager.ui.components.TextFieldTransparent
import com.ifucolo.taskmanager.ui.hexToColor
import com.ifucolo.taskmanager.ui.screens.addtask.category.CreateCategoryBottomSheet
import com.ifucolo.taskmanager.viewmodel.ManageTaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: ManageTaskViewModel,
    navController: NavController,
    taskId: Int? = null
) {
    val categories by viewModel.categories.collectAsState()

    val taskTitle by viewModel.taskTitle.collectAsState()
    val taskDescription by viewModel.taskDescription.collectAsState()
    val taskCategoryId by viewModel.taskCategoryId.collectAsState()
    val isValidTask = taskTitle.isNotEmpty()

    var backgroundColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Transparent) }
    val defaultColor = MaterialTheme.colorScheme.background
    val containerColor by animateColorAsState(
        targetValue = if (backgroundColor == Color.Transparent) {
            defaultColor
        } else {
            backgroundColor
        },
        label = "containerColor"
    )

    val bottomSheetState = rememberModalBottomSheetState()
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }

    val isEdit: Boolean = taskId !== null

    LaunchedEffect(taskId) {
        taskId?.let { id ->
            viewModel.findTaskById(id)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.add_task),
                onBackClick = { navController.navigateUp() },
                backgroundColor = containerColor
            )
        },
        containerColor = containerColor,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) { innerPadding ->

        if (openBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    if (taskCategoryId == null) {
                        backgroundColor = defaultColor
                    }
                    openBottomSheet.value = false
                },
                sheetState = bottomSheetState,
                containerColor = containerColor
            ) {
                CreateCategoryBottomSheet(
                    currentColor = backgroundColor,
                    onColorChange = {
                        backgroundColor = it
                    },
                    onSaveCategory = { category ->
                        hexToColor(
                            hex = category.color
                        )?.let { updatedColor ->
                            backgroundColor = updatedColor
                        }

                        openBottomSheet.value = false
                        viewModel.addCategory(
                            category = category,
                            onAdded = {
                                viewModel.setTaskCategoryId(it)
                            }
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp), // Center TextFields in the middle of the screen
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldTransparent(
                textValue = taskTitle,
                onTextChanged = viewModel::setTaskTitle,
                imeAction = ImeAction.Next,
                label = stringResource(id = R.string.title)
            )
            TextFieldTransparent(
                textValue = taskDescription,
                onTextChanged = viewModel::setTaskDescription,
                label = stringResource(id = R.string.description)
            )

            CategorySelector(
                categories = categories,
                selectedCategoryId = taskCategoryId,
                onCategorySelected = { category ->
                    viewModel.setTaskCategoryId(category.id)
                    hexToColor(hex = category.color)?.let { updatedColor ->
                        backgroundColor = updatedColor
                    }
                },
                onAddCategory = {
                    openBottomSheet.value = true
                })
        }
        PrimaryButton(
            label = stringResource(id = R.string.add_task),
            enabled = isValidTask,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onClick = {
                viewModel.handleSaveTask(
                    taskId = taskId,
                    isEdit = isEdit
                )
                navController.navigateUp()
            }
        )
    }
}
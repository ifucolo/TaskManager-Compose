package com.ifucolo.taskmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifucolo.taskmanager.data.local.entities.TaskWithCategory


@Composable
fun TaskItem(
    task: TaskWithCategory,
    onTaskClick: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissState ->
            if (dismissState === SwipeToDismissBoxValue.EndToStart) {
                onDeleteTask()
            }
            return@rememberSwipeToDismissBoxState false
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        enableDismissFromStartToEnd = false,
        backgroundContent = { DismissBackground(dismissState = swipeToDismissBoxState) }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onTaskClick() },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(8.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Label(
                    text = task.task.title,
                )
                if (task.task.description.isNotBlank()) {
                    Label(
                        text = task.task.description,
                        maxLines = 4
                    )
                }
                task.category?.let { category ->
                    CategoryItem(
                        category = category,
                        isSelected = true,
                        onSelectCategory = {}
                    )
                }
            }
        }
    }
}

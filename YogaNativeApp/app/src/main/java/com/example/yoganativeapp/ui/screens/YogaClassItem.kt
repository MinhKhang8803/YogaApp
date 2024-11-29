package com.example.yoganativeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yoganativeapp.data.YogaClass

@Composable
fun YogaClassItem(
    yogaClass: YogaClass,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Day: ${yogaClass.dayOfWeek}")
            Text(text = "Time: ${yogaClass.time}")
            Text(text = "Type: ${yogaClass.type}")
            Text(text = "Price: £${yogaClass.price}")
            Text(text = "Capacity: ${yogaClass.capacity}")
            Text(text = "Duration: ${yogaClass.duration} minutes")
            yogaClass.description?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Description: $it")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

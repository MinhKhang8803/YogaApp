package com.example.yoganativeapp.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yoganativeapp.data.YogaClass
import com.example.yoganativeapp.data.YogaDatabaseHelper
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClassScreen(navController: NavController, classId: String) {
    val context = LocalContext.current
    val dbHelper = remember { YogaDatabaseHelper(context) }
    val existingClass = dbHelper.getAllYogaClasses().find { it.id == classId }

    var dayOfWeekExpanded by remember { mutableStateOf(false) }
    var dayOfWeek by remember { mutableStateOf(existingClass?.dayOfWeek ?: "Select Day") }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    var time by remember { mutableStateOf(existingClass?.time ?: "Select Time") }
    var capacity by remember { mutableStateOf(existingClass?.capacity.toString()) }
    var duration by remember { mutableStateOf(existingClass?.duration.toString()) }
    var price by remember { mutableStateOf(existingClass?.price.toString()) }
    var typeExpanded by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(existingClass?.type ?: "Select Type") }
    val typesOfYoga = listOf("Flow Yoga", "Aerial Yoga", "Family Yoga")
    var description by remember { mutableStateOf(existingClass?.description ?: "") }

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Class") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = {},
                    label = { Text("Day of the Week") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dayOfWeekExpanded = true },
                    readOnly = true,
                    isError = dayOfWeek == "Select Day"
                )
                DropdownMenu(
                    expanded = dayOfWeekExpanded,
                    onDismissRequest = { dayOfWeekExpanded = false }
                ) {
                    daysOfWeek.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                dayOfWeek = day
                                dayOfWeekExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Time of Class") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                time = String.format("%02d:%02d", selectedHour, selectedMinute)
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    },
                readOnly = true,
                isError = time == "Select Time"
            )

            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = capacity.isEmpty()
            )

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = duration.isEmpty()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price per Class (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = price.isEmpty()
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    label = { Text("Type of Class") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { typeExpanded = true },
                    readOnly = true,
                    isError = type == "Select Type"
                )
                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    typesOfYoga.forEach { yogaType ->
                        DropdownMenuItem(
                            text = { Text(yogaType) },
                            onClick = {
                                type = yogaType
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedClass = existingClass?.copy(
                        dayOfWeek = dayOfWeek,
                        time = time,
                        capacity = capacity.toIntOrNull() ?: 0,
                        duration = duration.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0,
                        type = type,
                        description = description
                    )
                    if (updatedClass != null) {
                        dbHelper.updateYogaClass(updatedClass)
                        Toast.makeText(context, "Class updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = dayOfWeek != "Select Day" && time != "Select Time" && capacity.isNotEmpty()
                        && duration.isNotEmpty() && price.isNotEmpty() && type != "Select Type"
            ) {
                Text("Save Changes")
            }
        }
    }
}

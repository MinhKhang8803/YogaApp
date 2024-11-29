package com.example.yoganativeapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yoganativeapp.data.ClassInstance
import com.example.yoganativeapp.data.YogaClass
import com.example.yoganativeapp.data.YogaDatabaseHelper
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { YogaDatabaseHelper(context) }
    var yogaClasses by remember { mutableStateOf(dbHelper.getAllYogaClasses()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun syncClasses() {
        scope.launch {
            isSyncing = true
            val success = syncToMongoDB(dbHelper)
            isSyncing = false

            val message = if (success) {
                "Sync successful!"
            } else {
                "Sync failed. Please check your data and try again."
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    val filteredClasses = remember(searchQuery, yogaClasses) {
        yogaClasses.filter { yogaClass ->
            searchQuery.isBlank() ||
                    yogaClass.type.contains(searchQuery, ignoreCase = true) ||
                    (yogaClass.description?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search by class type or description") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    singleLine = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { syncClasses() },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Sync")
                    }
                    Button(onClick = { navController.navigate("add_class") }) {
                        Text("Add Class")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(filteredClasses) { yogaClass ->
                YogaClassItem(
                    navController = navController,
                    yogaClass = yogaClass,
                    dbHelper = dbHelper,
                    onRefreshClasses = {
                        yogaClasses = dbHelper.getAllYogaClasses()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaClassItem(
    navController: NavController,
    yogaClass: YogaClass,
    dbHelper: YogaDatabaseHelper,
    onRefreshClasses: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var instances by remember { mutableStateOf(dbHelper.getClassInstances(yogaClass.id!!)) }
    var showSchedule by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var numberOfSessions by remember { mutableStateOf("1") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    fun addInstances() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = sdf.parse(date) ?: return
        val calendar = Calendar.getInstance().apply { time = startDate }
        val dayOfWeekName = SimpleDateFormat("EEEE", Locale.getDefault()).format(startDate)

        if (dayOfWeekName != yogaClass.dayOfWeek) {
            Toast.makeText(context, "Invalid day! Please select a $dayOfWeekName", Toast.LENGTH_SHORT).show()
            return
        }

        repeat(numberOfSessions.toIntOrNull() ?: 1) {
            val newInstance = ClassInstance(
                yogaClassId = yogaClass.id ?: "",
                date = sdf.format(calendar.time),
                teacher = teacher,
                comments = if (comments.isBlank()) "No Comment" else comments
            )
            dbHelper.addClassInstance(newInstance)
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
        instances = dbHelper.getClassInstances(yogaClass.id!!)
        onRefreshClasses()
    }

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

            Row {
                Button(
                    onClick = { navController.navigate("edit_class/${yogaClass.id}") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Instance")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        dbHelper.deleteYogaClass(yogaClass.id!!)
                        onRefreshClasses()
                        Toast.makeText(context, "Class deleted", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Class")
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { },
                        label = { Text("Date (e.g. 01/10/2023)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        readOnly = true
                    )
                    OutlinedTextField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = { Text("Teacher") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("Comments (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = numberOfSessions,
                        onValueChange = { numberOfSessions = it },
                        label = { Text("Number of Sessions") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row {
                        Button(
                            onClick = { addInstances(); expanded = false },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Save")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { expanded = false },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Schedule",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showSchedule = !showSchedule }
            )
            if (showSchedule) {
                instances.forEach { instance ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Date: ${instance.date}, Teacher: ${instance.teacher}")
                        IconButton(onClick = {
                            dbHelper.deleteClassInstance(instance.id!!)
                            instances = dbHelper.getClassInstances(yogaClass.id!!)
                            onRefreshClasses()
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

suspend fun syncToMongoDB(dbHelper: YogaDatabaseHelper): Boolean {
    val yogaClasses = dbHelper.getAllYogaClasses()
    val classInstances = dbHelper.getAllClassInstances()

    val payload = mapOf(
        "yogaClasses" to yogaClasses.map { yogaClass ->
            mapOf(
                "id" to yogaClass.id,
                "dayOfWeek" to yogaClass.dayOfWeek,
                "time" to yogaClass.time,
                "capacity" to yogaClass.capacity,
                "duration" to yogaClass.duration,
                "price" to yogaClass.price,
                "type" to yogaClass.type,
                "description" to (yogaClass.description ?: "No description available")
            )
        },
        "classInstances" to classInstances.map { instance ->
            mapOf(
                "id" to instance.id,
                "yogaClassId" to instance.yogaClassId,
                "date" to instance.date,
                "teacher" to instance.teacher,
                "comments" to instance.comments
            )
        }
    )

    Log.d("SYNC_DEBUG", "Payload: ${payload.toString()}")

    return try {
        withContext(Dispatchers.IO) {
            val url = URL("https://yogabackend-z0rp.onrender.com/sync")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                doOutput = true
                doInput = true
            }

            val payloadJson = JSONObject(payload).toString()
            Log.d("SYNC_DEBUG", "Payload JSON: $payloadJson")

            connection.outputStream.use { os ->
                os.write(payloadJson.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

            Log.d("SYNC_DEBUG", "Response Code: $responseCode")
            Log.d("SYNC_DEBUG", "Response Message: $responseMessage")

            responseCode == HttpURLConnection.HTTP_OK
        }
    } catch (e: Exception) {
        Log.e("SYNC_ERROR", "Error syncing to MongoDB", e)
        false
    }
}



package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Навигация: login -> tasks -> profile (-> settings) / stats
                    var screen by rememberSaveable { mutableStateOf("login") }
                    var userEmail by rememberSaveable { mutableStateOf("") }
                    when (screen) {
                        "login" -> LoginScreen(onLogin = { email ->
                            userEmail = email
                            screen = "tasks"
                        })
                        "profile" -> {
                            BackHandler { screen = "tasks" }
                            ProfileScreen(
                                name = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                                email = userEmail,
                                onBack = { screen = "tasks" },
                                onSettings = { screen = "settings" },
                                onLogout = { userEmail = ""; screen = "login" }
                            )
                        }
                        "settings" -> {
                            BackHandler { screen = "profile" }
                            SettingsScreen(
                                darkTheme = darkTheme,
                                onDarkThemeChange = { darkTheme = it },
                                onBack = { screen = "profile" }
                            )
                        }
                        "stats" -> {
                            BackHandler { screen = "tasks" }
                            StatsScreen(onBack = { screen = "tasks" })
                        }
                        else -> TaskListScreen(
                            onOpenProfile = { screen = "profile" },
                            onOpenStats = { screen = "stats" }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(onOpenProfile: () -> Unit = {}, onOpenStats: () -> Unit = {}) {
    val tasks = remember { mutableStateListOf<Task>() }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Serverdan ro'yxatni to'liq qayta yuklash — har bir amaldan keyin chaqiriladi
    suspend fun loadTasks() {
        try {
            val result = RetrofitClient.api.getTasks()
            tasks.clear()
            tasks.addAll(result)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Ошибка подключения к серверу: ${e.message}"
        }
    }

    // Ekran birinchi ochilganda — fon oqimida GET /tasks so'rovini yuborish
    LaunchedEffect(Unit) {
        loadTasks()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задачи", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Статистика")
                    }
                    IconButton(onClick = onOpenProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Профиль")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Yangi vazifa qo'shish qatori
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Новая задача") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val title = inputText.trim()
                            inputText = ""
                            coroutineScope.launch {
                                try {
                                    RetrofitClient.api.createTask(CreateTaskRequest(title))
                                    loadTasks()
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка при добавлении: ${e.message}"
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добавить")
                }
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                tasks.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Пока задач нет",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggleDone = {
                            val newDone = if (task.isDone) 0 else 1
                            coroutineScope.launch {
                                try {
                                    RetrofitClient.api.updateDone(task.id, UpdateDoneRequest(newDone))
                                    loadTasks()
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка при отметке: ${e.message}"
                                }
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                try {
                                    RetrofitClient.api.deleteTask(task.id)
                                    loadTasks()
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка при удалении: ${e.message}"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(task: Task, onToggleDone: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Checkbox(checked = task.isDone, onCheckedChange = { onToggleDone() })
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isDone)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

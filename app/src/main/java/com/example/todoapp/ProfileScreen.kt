package com.example.todoapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Profil ekrani — chizmadagi joylashuv bo'yicha:
// sarlavha + orqaga, rasm (avatar), Ism Familiya, email, 3 ta menyu qatori, pastda "Редактировать" tugmasi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String = "Имя Фамилия",
    email: String = "email@mail.com",
    onBack: () -> Unit,
    onSettings: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Профиль", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // Rasm (avatar)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Фото",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(email, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            ProfileMenuItem(Icons.Default.Settings, "Настройки", onSettings)
            ProfileMenuItem(Icons.Default.Notifications, "Уведомления", onNotifications)
            ProfileMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Выйти", onLogout)

            Spacer(Modifier.weight(1f))

            // "Редактировать" tugmasi
            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Редактировать", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Medium)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

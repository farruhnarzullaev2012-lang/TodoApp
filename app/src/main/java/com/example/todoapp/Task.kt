package com.example.todoapp

data class Task(
    val id: Int,
    val title: String,
    val done: Int // backend 0/1 qaytaradi; UI'da isDone orqali Boolean sifatida ishlatiladi
) {
    val isDone: Boolean get() = done != 0
}

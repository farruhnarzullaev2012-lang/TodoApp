package com.example.todoapp

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class CreateTaskRequest(val title: String)
data class UpdateDoneRequest(val done: Int)

interface ApiService {
    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun createTask(@Body body: CreateTaskRequest): Task

    @PATCH("tasks/{id}")
    suspend fun updateDone(@Path("id") id: Int, @Body body: UpdateDoneRequest): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}

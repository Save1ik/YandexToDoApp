package com.example.yandextodo.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("list")
    suspend fun getToDoList(): Response<ToDoListResponse>

    @PATCH("list")
    suspend fun updateToDoList(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ToDoListRequest
    ): Response<ToDoListResponse>


    @POST("list")
    suspend fun addToDoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ToDoItemElementRequest
    ): Response<ToDoElementResponse>


    @PUT("list/{id}")
    suspend fun updateToDoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ToDoItemUpdateRequest
    ): Response<ToDoElementResponse>

    @DELETE("list/{id}")
    suspend fun deleteToDoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int
    ): Response<ToDoElementResponse>
}

class ToDoApiService(baseUrl: String, bearerToken: String) {
    private val api: ApiService

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $bearerToken")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    suspend fun getToDoList(): Result<ToDoListResponse> {
        return try {
            val response = api.getToDoList()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to get list: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateToDoList(revision: Int, request: ToDoListRequest): Result<ToDoListResponse> {
        return try {
            val response = api.updateToDoList(revision, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to update list: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToDoItem(revision: Int, request: ToDoItemElementRequest): Result<ToDoElementResponse> {
        return try {
            println("Отправка POST запроса с ревизией: $revision")
            println("Тело запроса: $request")
            val response = api.addToDoItem(revision, request)
            println("Ответ POST: ${response.code()}")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                println("Ошибка POST: ${response.code()} $errorBody")
                Result.failure(Exception("Failed to add item: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            println("Исключение в POST: ${e.message}")
            Result.failure(e)
        }
    }


    suspend fun updateToDoItem(id: String, revision: Int, request: ToDoItemUpdateRequest): Result<ToDoElementResponse> {
        return try {
            val response = api.updateToDoItem(id, revision, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to update item: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteToDoItem(id: String, revision: Int): Result<ToDoElementResponse> {
        return try {
            val response = api.deleteToDoItem(id, revision)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to delete item: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
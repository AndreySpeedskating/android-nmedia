package ru.netology.nmedia.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val posts = mutableListOf<Post>()
    private val dataFlow = MutableStateFlow(posts.toList())

    override val data: Flow<List<Post>> = dataFlow


    private val api = run {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9999/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PostsApiService::class.java)
    }

    override suspend fun getAll() {
        try {
            val response = api.getAllPosts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    posts.clear()
                    posts.addAll(body)
                    dataFlow.emit(posts.toList())
                    Log.d("PostRepository", "Posts loaded: ${posts.size}")
                }
            } else {
                Log.e("PostRepository", "Error loading posts: ${response.code()}")
                throw HttpException(response)
            }
        } catch (e: IOException) {
            Log.e("PostRepository", "Network error loading posts", e)
            throw IOException("Нет подключения к интернету")
        } catch (e: HttpException) {
            Log.e("PostRepository", "HTTP error loading posts", e)
            throw e
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception loading posts", e)
            throw Exception("Неизвестная ошибка: ${e.message}")
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val currentPost = posts.find { it.id == id }

            val response = if (currentPost?.likedByMe == true) {
                api.dislikeById(id)
            } else {
                api.likeById(id)
            }

            if (response.isSuccessful) {
                val updatedPost = response.body()
                if (updatedPost != null) {
                    val index = posts.indexOfFirst { it.id == id }
                    if (index != -1) {
                        posts[index] = updatedPost
                    }
                }
            } else {
                Log.e("PostRepository", "Error liking post: ${response.code()}")
                throw HttpException(response)
            }
        } catch (e: IOException) {
            Log.e("PostRepository", "Network error liking post", e)
            throw IOException("Нет подключения к интернету")
        } catch (e: HttpException) {
            Log.e("PostRepository", "HTTP error liking post", e)
            throw e
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception liking post", e)
            throw Exception("Неизвестная ошибка: ${e.message}")
        }
    }

    override suspend fun shareById(id: Long) {
        try {
            val index = posts.indexOfFirst { it.id == id }
            if (index != -1) {
                val currentPost = posts[index]
                posts[index] = currentPost.copy(shares = currentPost.shares + 1)
                Log.d("PostRepository", "Shared post with id: $id")
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception sharing post", e)
            throw Exception("Ошибка при шеринге: ${e.message}")
        }
    }

    override suspend fun save(post: Post) {
        try {
            if (post.id == 0L) {
                val response = api.savePost(post)
                if (response.isSuccessful) {
                    val newPost = response.body()
                    if (newPost != null) {
                        posts.add(0, newPost)
                    }
                } else {
                    throw HttpException(response)
                }
            } else {
                update(post)
            }
        } catch (e: IOException) {
            Log.e("PostRepository", "Network error saving post", e)
            throw IOException("Нет подключения к интернету")
        } catch (e: HttpException) {
            Log.e("PostRepository", "HTTP error saving post", e)
            throw e
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception saving post", e)
            throw Exception("Неизвестная ошибка: ${e.message}")
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = api.removeById(id)
            if (response.isSuccessful) {
                posts.removeAll { it.id == id }
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            Log.e("PostRepository", "Network error removing post", e)
            throw IOException("Нет подключения к интернету")
        } catch (e: HttpException) {
            Log.e("PostRepository", "HTTP error removing post", e)
            throw e
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception removing post", e)
            throw Exception("Неизвестная ошибка: ${e.message}")
        }
    }

    override suspend fun update(post: Post) {
        try {
            val response = api.updatePost(post.id, post)
            if (response.isSuccessful) {
                val updatedPost = response.body()
                if (updatedPost != null) {
                    val index = posts.indexOfFirst { it.id == post.id }
                    if (index != -1) {
                        posts[index] = updatedPost
                    }
                }
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            Log.e("PostRepository", "Network error updating post", e)
            throw IOException("Нет подключения к интернету")
        } catch (e: HttpException) {
            Log.e("PostRepository", "HTTP error updating post", e)
            throw e
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception updating post", e)
            throw Exception("Неизвестная ошибка: ${e.message}")
        }
    }

    override suspend fun getById(id: Long): Post? {
        return try {
            posts.find { it.id == id }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception getting post by id", e)
            null
        }
    }
}
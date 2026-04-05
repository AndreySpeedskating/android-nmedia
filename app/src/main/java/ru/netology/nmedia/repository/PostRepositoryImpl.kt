package ru.netology.nmedia.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private var posts = emptyList<Post>()
    private val dataFlow = MutableStateFlow(posts)

    override val data: Flow<List<Post>> = dataFlow

    private val api = PostsApi.retrofitService

    override suspend fun getAll() {
        try {
            val response = api.getAllPosts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    posts = body.toList() // Создаём новый неизменяемый список
                    dataFlow.emit(posts) // Отправляем новую ссылку
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
                    // Создаём новый список с обновлённым постом
                    posts = posts.map { post ->
                        if (post.id == id) updatedPost else post
                    }
                    dataFlow.emit(posts)
                    Log.d("PostRepository", "Post liked/disliked: $id")
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
            val currentPost = posts.find { it.id == id }
            if (currentPost != null) {
                // Создаём новый список с обновлённым счётчиком шаров
                posts = posts.map { post ->
                    if (post.id == id) post.copy(shares = post.shares + 1) else post
                }
                dataFlow.emit(posts)
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
                        // Создаём новый список с добавленным постом в начало
                        posts = listOf(newPost) + posts
                        dataFlow.emit(posts)
                        Log.d("PostRepository", "Post saved: ${newPost.id}")
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
                // Создаём новый список без удалённого поста
                posts = posts.filter { it.id != id }
                dataFlow.emit(posts)
                Log.d("PostRepository", "Post removed: $id")
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
                    // Создаём новый список с обновлённым постом
                    posts = posts.map { existingPost ->
                        if (existingPost.id == post.id) updatedPost else existingPost
                    }
                    dataFlow.emit(posts)
                    Log.d("PostRepository", "Post updated: ${post.id}")
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
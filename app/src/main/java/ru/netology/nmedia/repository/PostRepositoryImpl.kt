package ru.netology.nmedia.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val posts = mutableListOf<Post>()
    private val dataFlow = flow {
        emit(posts.toList())
    }.flowOn(Dispatchers.Default)

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
                    Log.d("PostRepository", "Posts loaded: ${posts.size}")
                }
            } else {
                Log.e("PostRepository", "Error loading posts: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception loading posts", e)
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            // Находим пост, чтобы узнать, лайкнут ли он сейчас
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
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception liking post", e)
        }
    }

    override suspend fun shareById(id: Long) {
        try {
            // Для шаринга пока просто увеличиваем счетчик локально
            val index = posts.indexOfFirst { it.id == id }
            if (index != -1) {
                val currentPost = posts[index]
                posts[index] = currentPost.copy(shares = currentPost.shares + 1)
                Log.d("PostRepository", "Shared post with id: $id")
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception sharing post", e)
        }
    }

    override suspend fun save(post: Post) {
        try {
            // Для сохранения нового поста
            if (post.id == 0L) {
                // Здесь должен быть POST запрос на сервер для создания нового поста
                // Пока добавляем локально с временным ID
                val newPost = post.copy(id = System.currentTimeMillis())
                posts.add(0, newPost)
                Log.d("PostRepository", "Saved new post with id: ${newPost.id}")
            } else {
                // Обновление существующего поста
                update(post)
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception saving post", e)
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            // Здесь должен быть DELETE запрос на сервер
            posts.removeAll { it.id == id }
            Log.d("PostRepository", "Removed post with id: $id")
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception removing post", e)
        }
    }

    override suspend fun update(post: Post) {
        try {
            val index = posts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                posts[index] = post
                Log.d("PostRepository", "Updated post with id: ${post.id}")
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Exception updating post", e)
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
package ru.netology.nmedia.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PostRepositoryJsonImpl(private val context: Context) : PostRepository {
    private val gson = Gson()
    private val type = object : TypeToken<List<Post>>() {}.type
    private val dataFile = File(context.filesDir, "posts.json")

    private var posts = loadPosts()
    private var nextId = posts.maxOfOrNull { it.id }?.plus(1) ?: 1L

    private val _data = MutableStateFlow(posts)
    override val data = _data.asStateFlow()

    private fun loadPosts(): List<Post> {
        return if (dataFile.exists()) {
            try {
                FileInputStream(dataFile).bufferedReader().use {
                    gson.fromJson(it, type) ?: getDefaultPosts()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getDefaultPosts()
            }
        } else {
            getDefaultPosts()
        }
    }

    private fun getDefaultPosts(): List<Post> {
        return listOf(
            Post(
                id = 1L,
                author = "Нетология",
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                published = "23 мая в 18:36",
                likedByMe = true,
                likes = 125,
                shares = 5,
                views = 1500,
                authorAvatar = R.drawable.netology_avatar.toString(),
                video = "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
            ),
            Post(
                id = 2L,
                author = "Иван Иванов",
                content = "Задняя крышка смартфона получила двухфактурную отделку: матовый низ сочетается с полупрозрачным верхом. На выбор предложат три цвета: серебристый, оранжевый и синий.",
                published = "23 мая в 19:00",
                likedByMe = false,
                likes = 999,
                shares = 1200,
                views = 25000,
                authorAvatar = R.drawable.netology_avatar.toString(),
            ),
            Post(
                id = 3L,
                author = "Петр Петров",
                content = "Компания Nvidia недавно заявила, что массовое производство ускорителей для ИИ поколения Vera Rubin стартует уже в первом квартале, то есть с опережением сроков.",
                published = "23 мая в 19:15",
                likedByMe = false,
                likes = 999,
                shares = 999,
                views = 1300000,
                authorAvatar = R.drawable.netology_avatar.toString(),
                video = "https://rutube.ru/video/private/0dc6cfcd8b2c38af84a9b5b7f0fa7b4b/?p=WJSCSTVrCahdGZxVH-NjJg"
            )
        )
    }

    private fun savePosts() {
        try {
            FileOutputStream(dataFile).bufferedWriter().use {
                gson.toJson(posts, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAll(): List<Post> = posts

    override fun getById(id: Long): Post? = posts.find { it.id == id }

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                val newLikedByMe = !post.likedByMe
                val newLikesCount = if (newLikedByMe) post.likes + 1 else post.likes - 1
                post.copy(likedByMe = newLikedByMe, likes = newLikesCount)
            } else {
                post
            }
        }
        savePosts()
        _data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(shares = post.shares + 1)
            } else {
                post
            }
        }
        savePosts()
        _data.value = posts
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            val newPost = post.copy(id = nextId++)
            (listOf(newPost) + posts).toMutableList()
        } else {
            posts.map { if (it.id == post.id) post else it }.toMutableList()
        }
        savePosts()
        _data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }.toMutableList()
        savePosts()
        _data.value = posts
    }

    override fun update(post: Post) {
        save(post)
    }
}
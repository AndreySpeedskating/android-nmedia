package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L
    private var posts = mutableListOf(
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "23 мая в 18:36",
            likedByMe = true,
            likesCount = 125,
            sharesCount = 5,
            viewsCount = 1500,
            avatar = R.drawable.netology_avatar,
            video = "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
        ),
        Post(
            id = nextId++,
            author = "Иван Иванов",
            content = "Задняя крышка смартфона получила двухфактурную отделку: матовый низ сочетается с полупрозрачным верхом. На выбор предложат три цвета: серебристый, оранжевый и синий.",
            published = "23 мая в 19:00",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 1200,
            viewsCount = 25000,
            avatar = R.drawable.netology_avatar
        ),
        Post(
            id = nextId++,
            author = "Петр Петров",
            content = "Компания Nvidia недавно заявила, что массовое производство ускорителей для ИИ поколения Vera Rubin стартует уже в первом квартале, то есть с опережением сроков.",
            published = "23 мая в 19:15",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 999,
            viewsCount = 1300000,
            avatar = R.drawable.netology_avatar,
            video = "https://rutube.ru/video/private/0dc6cfcd8b2c38af84a9b5b7f0fa7b4b/?p=WJSCSTVrCahdGZxVH-NjJg"
        )
    )

    private val _data = MutableStateFlow(posts)
    override val data: Flow<List<Post>> = _data

    override fun getAll(): List<Post> = posts

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                val newLikedByMe = !post.likedByMe
                val newLikesCount = if (newLikedByMe) post.likesCount + 1 else post.likesCount - 1
                post.copyWithLike(newLikedByMe, newLikesCount)
            } else {
                post
            }
        }.toMutableList()
        _data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copyWithShare(post.sharesCount + 1)
            } else {
                post
            }
        }.toMutableList()
        _data.value = posts
    }

    override fun save(post: Post) {

        if (post.id == 0L) {
            // Создание нового поста
            val newPost = post.copy(id = nextId++)
            posts = (listOf(newPost) + posts).toMutableList()
        } else {
            // Редактирование существующего поста
            val index = posts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                posts = posts.toMutableList().apply {
                    this[index] = post
                }
            } else {
                println("POST NOT FOUND for id=${post.id}")
            }
        }
        _data.value = posts
    }

    override fun update(post: Post) {
        val index = posts.indexOfFirst { it.id == post.id }
        if (index != -1) {
            posts = posts.toMutableList().apply {
                this[index] = post
            }
            _data.value = posts
        }
    }


    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }.toMutableList()
        _data.value = posts
    }

    override fun getById(id: Long): Post? {
        return posts.find { it.id == id }
    }
}
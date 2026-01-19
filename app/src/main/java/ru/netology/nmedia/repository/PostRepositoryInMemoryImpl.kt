package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L

    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Кто то здесь",
            content = "Что то здесь!",
            published = "23 мая в 18:36",
            likedByMe = true,
            likesCount = 125,
            sharesCount = 5,
            viewsCount = 1500,
            avatar = R.drawable.netology_avatar
        ),
        Post(
            id = nextId++,
            author = "Кто то там",
            content = "Что то там!",
            published = "23 мая в 19:00",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 1200,
            viewsCount = 25000,
            avatar = R.drawable.netology_avatar
        ),
        Post(
            id = nextId++,
            author = "Кто то тут",
            content = "Что то тут!",
            published = "23 мая в 19:15",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 999,
            viewsCount = 1300000,
            avatar = R.drawable.netology_avatar
        )
    )

    private val _data = MutableStateFlow(posts)

    override val data: Flow<List<Post>> = _data

    override fun getAll(): List<Post> = posts

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(
                    likedByMe = !post.likedByMe,
                    likesCount = if (!post.likedByMe) post.likesCount + 1 else post.likesCount - 1
                )
            } else {
                post
            }
        }
        _data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(sharesCount = post.sharesCount + 1)
            } else {
                post
            }
        }
        _data.value = posts
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = nextId++)) + posts
        } else {
            posts.map { if (it.id == post.id) post else it }
        }
        _data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        _data.value = posts
    }
}
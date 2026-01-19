package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.utils.NumberFormatter

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    val data = repository.data.map { posts  ->
        posts.map { post ->
            post.copy(
                likesCount = post.likesCount,
                sharesCount = post.sharesCount,
                viewsCount = post.viewsCount
            )
        }
    }.asLiveData()

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun save(post: Post) = repository.save(post)
    fun removeById(id: Long) = repository.removeById(id)

    fun formatLikesCount(count: Int): String = NumberFormatter.formatCompact(count)
    fun formatSharesCount(count: Int): String = NumberFormatter.formatCompact(count)
    fun formatViewsCount(count: Int): String = NumberFormatter.formatCompact(count)
}
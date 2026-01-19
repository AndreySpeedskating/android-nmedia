package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val avatar: Int,
    val published: String,
    var likedByMe: Boolean = false,
    var likesCount: Int = 0,
    var sharesCount: Int = 0,
    var viewsCount: Int = 0
)

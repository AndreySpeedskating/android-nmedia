package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val avatar: Int,
    var likedByMe: Boolean = false,
    var likesCount: Int = 0,
    var sharesCount: Int = 0,
    var viewsCount: Int = 0
) {
    companion object {
        val empty = Post(
            id = 0,
            author = "",
            content = "",
            published = "",
            avatar = android.R.drawable.sym_def_app_icon
        )
    }
}
package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val avatar: Int = android.R.drawable.sym_def_app_icon,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0
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

    // Методы для создания копий с измененными значениями
    fun copyWithLike(newLikedByMe: Boolean, newLikesCount: Int): Post {
        return this.copy(
            likedByMe = newLikedByMe,
            likesCount = newLikesCount
        )
    }

    fun copyWithShare(newSharesCount: Int): Post {
        return this.copy(sharesCount = newSharesCount)
    }

    fun copyWithContent(newContent: String): Post {
        return this.copy(content = newContent)
    }
}
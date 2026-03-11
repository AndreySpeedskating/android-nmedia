package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: String? = null,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val attachment: Attachment? = null
) {
    companion object {
        val empty = Post(
            id = 0,
            author = "",
            content = "",
            published = "",
            authorAvatar = null
        )
    }

    // Методы для создания копий с измененными значениями
    fun copyWithLike(newLikedByMe: Boolean, newLikesCount: Int): Post {
        return this.copy(
            likedByMe = newLikedByMe,
            likes = newLikesCount
        )
    }

    fun copyWithShare(newSharesCount: Int): Post {
        return this.copy(shares = newSharesCount)
    }

    fun copyWithContent(newContent: String): Post {
        return this.copy(content = newContent)
    }
}

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}
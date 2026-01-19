package ru.netology.nmedia

import java.util.Date

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: Int,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likesCount: Int = 0,
    var sharesCount: Int = 0,
    var viewsCount: Int = 0
) {
    companion object {
        val samplePosts = listOf(
            Post(
                id = 1,
                author = "Нетология. Университет интернет-профессий",
                authorAvatar = R.drawable.netology_avatar,
                content = "Привет, это новая Нетология!",
                published = "21 мая в 18:36",
                likedByMe = true,
                likesCount = 125,
                sharesCount = 5,
                viewsCount = 1500
            ),
            Post(
                id = 2,
                author = "Илон Маск",
                authorAvatar = R.drawable.netology_avatar,
                content = "SpaceX запустила новую ракету!",
                published = "22 мая в 12:00",
                likedByMe = false,
                likesCount = 999,
                sharesCount = 120,
                viewsCount = 25000
            ),
            Post(
                id = 3,
                author = "Android Developer",
                authorAvatar = R.drawable.netology_avatar,
                content = "Новая версия Android 14 вышла!",
                published = "23 мая в 09:15",
                likedByMe = false,
                likesCount = 9999,
                sharesCount = 999,
                viewsCount = 1300000
            )
        )
    }
}
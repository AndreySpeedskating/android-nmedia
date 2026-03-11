package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.GET
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @GET("posts")
    suspend fun getAllPosts(): Response<List<Post>>
}
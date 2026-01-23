package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.activity.NMediaApplication
import ru.netology.nmedia.dto.Post

class PostViewModel : ViewModel() {
    private val repository = NMediaApplication.repository

    val data: LiveData<List<Post>> = repository.data.asLiveData()

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun save(post: Post) = repository.save(post)
}
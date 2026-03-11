package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.NMediaApplication
import ru.netology.nmedia.dto.Post

class PostViewModel : ViewModel() {
    private val repository = NMediaApplication.repository

    val data: LiveData<List<Post>> = repository.data.asLiveData()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            repository.getAll()
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            repository.likeById(id)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun save(post: Post) {
        viewModelScope.launch {
            repository.save(post)
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            repository.shareById(id)
        }
    }
}
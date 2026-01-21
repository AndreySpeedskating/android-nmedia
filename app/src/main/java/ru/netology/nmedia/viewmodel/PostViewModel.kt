package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    val data: LiveData<List<Post>> = repository.data.asLiveData()

    private val _edited = MutableLiveData<Post?>(null)
    val edited: LiveData<Post?> = _edited

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)

    fun save() {
        println("📱 VIEWMODEL save() called")
        println("📱 _edited.value: ${_edited.value}")
        _edited.value?.let {
            repository.save(it)
            _edited.value = null
        }
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) {
            return
        }
        _edited.value = _edited.value?.copy(content = text)
    }

    fun cancelEditing() {
        _edited.value = null
    }

    fun removeById(id: Long) = repository.removeById(id)
}
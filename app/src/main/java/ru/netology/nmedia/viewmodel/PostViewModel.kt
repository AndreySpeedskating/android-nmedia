package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.NMediaApplication
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.DataState

class PostViewModel : ViewModel() {
    private val repository = NMediaApplication.repository

    val data: LiveData<List<Post>> = repository.data.asLiveData()

    private val _dataState = MutableLiveData<DataState>(DataState.Success)
    val dataState: LiveData<DataState> = _dataState

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.value = DataState.Loading
                repository.getAll()
                _dataState.value = DataState.Success
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    message = "Ошибка загрузки: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = DataState.Loading
                repository.likeById(id)
                _dataState.value = DataState.Success
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    message = "Ошибка при лайке: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    message = "Ошибка при шеринге: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = DataState.Loading
                repository.removeById(id)
                _dataState.value = DataState.Success
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    message = "Ошибка при удалении: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    fun save(post: Post) {
        viewModelScope.launch {
            try {
                _dataState.value = DataState.Loading
                repository.save(post)
                _dataState.value = DataState.Success
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    message = "Ошибка при сохранении: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    fun retry() {
        loadPosts()
    }

    fun resetError() {
        _dataState.value = DataState.Success
    }
}
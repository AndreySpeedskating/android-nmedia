package ru.netology.nmedia.model

sealed class DataState {
    object Success : DataState()
    object Loading : DataState()
    data class Error(val message: String, val canRetry: Boolean = true) : DataState()
}

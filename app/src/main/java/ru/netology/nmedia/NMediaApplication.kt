package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class NMediaApplication : Application() {
    companion object {
        lateinit var repository: PostRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        repository = PostRepositoryImpl()
    }
}
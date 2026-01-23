package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class NMediaApplication : Application() {
    companion object {
        lateinit var repository: PostRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        repository = PostRepositoryInMemoryImpl()
    }
}

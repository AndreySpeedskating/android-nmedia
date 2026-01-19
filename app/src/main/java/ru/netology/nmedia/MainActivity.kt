package ru.netology.nmedia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создаем адаптер
        val adapter = PostAdapter(Post.samplePosts, object : PostAdapter.OnInteractionListener {
            override fun onLike(post: Post) {
                Log.d("MainActivity", "Лайк на посте ${post.id}")
            }

            override fun onShare(post: Post) {
                Log.d("MainActivity", "Шеринг поста ${post.id}")
            }

            override fun onMenu(post: Post) {
                Log.d("MainActivity", "Меню поста ${post.id}")
            }
        })

        // Настраиваем RecyclerView
        binding.postsList.layoutManager = LinearLayoutManager(this)
        binding.postsList.adapter = adapter
    }
}
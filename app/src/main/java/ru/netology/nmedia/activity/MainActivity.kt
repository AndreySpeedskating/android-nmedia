package ru.netology.nmedia.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onLike(post: ru.netology.nmedia.dto.Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: ru.netology.nmedia.dto.Post) {
                viewModel.shareById(post.id)
                Toast.makeText(
                    this@MainActivity,
                    "Поделились записью \"${post.content.take(20)}...\"",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onMenu(post: ru.netology.nmedia.dto.Post) {
                Toast.makeText(
                    this@MainActivity,
                    "Меню поста ${post.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.postsList.layoutManager = LinearLayoutManager(this)
        binding.postsList.adapter = adapter

        // Подписываемся на изменения данных
        viewModel.data.observe(this) { posts: List<Post>? ->
            adapter.submitList(posts)
        }
    }
}
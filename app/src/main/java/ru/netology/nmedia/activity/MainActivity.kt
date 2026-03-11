package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.DataState
import ru.netology.nmedia.repository.NewPostContract
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val newPostLauncher = registerForActivityResult(NewPostContract()) { postId ->
        if (postId != null) {
            // Пост сохранен
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "Поделиться постом")
                startActivity(shareIntent)
            }

            override fun onEdit(post: Post) {
                newPostLauncher.launch(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onVideoClick(post: Post) {
                post.video?.let { videoUrl ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                    startActivity(intent)
                }
            }
        })

        binding.postsList.layoutManager = LinearLayoutManager(this)
        binding.postsList.adapter = adapter

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPosts()
        }

        // Кнопка добавления поста
        binding.fab.setOnClickListener {
            newPostLauncher.launch(null)
        }

        // Кнопка повторной попытки в ошибке
        binding.errorLayout.retryButton.setOnClickListener {
            viewModel.retry()
        }

        // Наблюдаем за состоянием данных
        viewModel.dataState.observe(this) { state ->
            when (state) {
                is DataState.Loading -> showLoading()
                is DataState.Success -> showContent()
                is DataState.Error -> showError(state.message)
            }
        }

        // Наблюдаем за списком постов
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.postsList.visibility = View.GONE
        binding.errorLayout.root.visibility = View.GONE
        binding.swipeRefreshLayout.isEnabled = false
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.postsList.visibility = View.VISIBLE
        binding.errorLayout.root.visibility = View.GONE
        binding.swipeRefreshLayout.isEnabled = true
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.postsList.visibility = View.GONE
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.errorLayout.errorMessage.text = message
        binding.swipeRefreshLayout.isEnabled = true
        binding.swipeRefreshLayout.isRefreshing = false
    }
}
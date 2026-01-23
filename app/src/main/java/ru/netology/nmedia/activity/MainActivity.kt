package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.NewPostContract
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val newPostLauncher = registerForActivityResult(NewPostContract()) { postId ->
        // Если вернулся ID, значит пост был сохранен
        if (postId != null) {
            // Можно обновить данные или что-то сделать
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
                // Передаем только ID поста
                newPostLauncher.launch(post?.id)
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

        binding.fab.setOnClickListener {
            // Для нового поста передаем null
            newPostLauncher.launch(null)
        }

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}
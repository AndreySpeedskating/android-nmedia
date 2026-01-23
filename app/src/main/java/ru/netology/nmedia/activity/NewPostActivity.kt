package ru.netology.nmedia.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPostBinding
    private val viewModel: PostViewModel by viewModels()
    private var postId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем ID поста из Intent
        postId = intent.getLongExtra(EXTRA_POST_ID, 0L)

        // Загружаем данные поста для редактирования
        if (postId != 0L) {
            loadPostForEditing()
            binding.toolbar.title = getString(R.string.edit_post)
        } else {
            binding.toolbar.title = getString(R.string.new_post)
        }

        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Обработка кнопки сохранения
        binding.save.setOnClickListener {
            savePost()
        }

        // Обработка кнопки отмены
        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun loadPostForEditing() {
        // Загружаем пост напрямую без наблюдения
        val currentPosts = viewModel.data.value
        val post = currentPosts?.find { it.id == postId }
        post?.let {
            binding.edit.setText(it.content)
            binding.videoUrl.setText(it.video ?: "")
        }
    }

    private fun savePost() {
        val content = binding.edit.text.toString().trim()
        val videoUrl = binding.videoUrl.text.toString().trim()

        if (content.isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_empty_content),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (postId != 0L) {
            // Редактирование существующего поста
            // Получаем текущий список постов
            val currentPosts = viewModel.data.value ?: return

            // Находим пост для редактирования
            val currentPost = currentPosts.find { it.id == postId }

            currentPost?.let { existingPost ->
                // Создаем обновленную копию поста
                val updatedPost = existingPost.copy(
                    content = content,
                    video = if (videoUrl.isNotBlank()) videoUrl else null
                )

                // Сохраняем обновленный пост
                viewModel.save(updatedPost)

                // Ждем немного, чтобы данные успели обновиться
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 100)
            } ?: run {
                // Если пост не найден, просто закрываем
                finish()
            }
        } else {
            // Создание нового поста
            val newPost = Post(
                id = 0L,
                author = "Netology",
                published = "только что",
                content = content,
                likedByMe = false,
                likesCount = 0,
                sharesCount = 0,
                viewsCount = 0,
                video = if (videoUrl.isNotBlank()) videoUrl else null,
                avatar = R.drawable.netology_avatar
            )
            viewModel.save(newPost)

            // Ждем немного, чтобы данные успели обновиться
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 100)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_POST_ID = "post_id"
    }
}
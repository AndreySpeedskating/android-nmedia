package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

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
                Toast.makeText(
                    this@MainActivity,
                    "Поделились записью",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onEdit(post: Post) {
                showEditDialog(post)
            }

            override fun onRemove(post: Post) {
                showDeleteDialog(post)
            }

            override fun onMenu(post: Post) {
                showMenuDialog(post)
            }
        })

        binding.postsList.layoutManager = LinearLayoutManager(this)
        binding.postsList.adapter = adapter

        // Кнопка добавления нового поста
        binding.fab.setOnClickListener {
            showEditDialog(Post.empty)
        }

        // Подписка на данные
        viewModel.data.observe(this) { posts ->
            println("📱 ACTIVITY: data changed! Posts count: ${posts.size}")
            println("📱 First post content: '${posts.firstOrNull()?.content?.take(30)}...'")
            adapter.submitList(posts)
        }

        // Наблюдение за редактируемым постом
        viewModel.edited.observe(this) { post ->
            if (post == null) {
                // Скрываем диалог редактирования
            }
        }
    }

    private fun showEditDialog(post: Post) {
        val dialogBinding = FragmentEditPostBinding.inflate(LayoutInflater.from(this))

        dialogBinding.content.setText(post.content)

        val isEditing = post.id != 0L
        dialogBinding.editingButtons.visibility =
            if (isEditing) View.VISIBLE else View.GONE

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEditing) R.string.post_edit_title else R.string.post_new_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.post_save) { _, _ ->
                val content = dialogBinding.content.text.toString()
                if (content.isNotBlank()) {
                    // 1. Создаем обновленную копию поста
                    val updatedPost = post.copyWithContent(content)
                    // 2. Передаем в ViewModel
                    viewModel.edit(updatedPost)
                    // 3. Сохраняем
                    viewModel.save()
                }
            }
            .setNegativeButton(R.string.post_cancel_button) { dialog, _ ->
                viewModel.cancelEditing()
                dialog.dismiss()
            }
            .create()

        dialog.show()

        dialogBinding.save.setOnClickListener {
            val content = dialogBinding.content.text.toString()
            if (content.isNotBlank()) {
                val updatedPost = post.copyWithContent(content)
                viewModel.edit(updatedPost)
                viewModel.save()
                dialog.dismiss()
            }
        }

        dialogBinding.cancel.setOnClickListener {
            viewModel.cancelEditing()
            dialog.dismiss()
        }
    }

    private fun showDeleteDialog(post: Post) {
        AlertDialog.Builder(this)
            .setTitle(R.string.post_delete_title)
            .setMessage(R.string.post_delete_message)
            .setPositiveButton(R.string.post_delete_confirm) { _, _ ->
                viewModel.removeById(post.id)
                if (viewModel.edited.value?.id == post.id) {
                    viewModel.cancelEditing()
                }
            }
            .setNegativeButton(R.string.post_cancel_button, null)
            .show()
    }

    private fun showMenuDialog(post: Post) {
        val items = arrayOf(
            getString(R.string.post_edit),
            getString(R.string.post_delete)
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.post_menu_title)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showEditDialog(post)
                    1 -> showDeleteDialog(post)
                }
            }
            .setNegativeButton(R.string.post_cancel_button, null)
            .show()
    }
}
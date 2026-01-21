package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.NumberFormatter

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    interface OnInteractionListener {
        fun onLike(post: Post)
        fun onShare(post: Post)
        fun onEdit(post: Post)
        fun onRemove(post: Post)
        fun onMenu(post: Post)
    }

    inner class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content

                // Устанавливаем аватар
                avatar.setImageResource(post.avatar)

                // Форматируем числа
                likeCount.text = NumberFormatter.formatCompact(post.likesCount)
                shareCount.text = NumberFormatter.formatCompact(post.sharesCount)
                viewsCount.text = NumberFormatter.formatCompact(post.viewsCount)

                // Устанавливаем иконку лайка
                val likeIcon = if (post.likedByMe) {
                    R.drawable.ic_liked_24
                } else {
                    R.drawable.ic_like_24
                }
                likeButton.setImageResource(likeIcon)

                // Обработчики событий
                likeButton.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                shareButton.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                editButton.setOnClickListener {
                    onInteractionListener.onEdit(post)
                }

                deleteButton.setOnClickListener {
                    onInteractionListener.onRemove(post)
                }

                menu.setOnClickListener {
                    onInteractionListener.onMenu(post)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
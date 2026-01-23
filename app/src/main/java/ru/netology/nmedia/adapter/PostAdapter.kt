package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
        fun onVideoClick(post: Post)
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
                likeButton.text = NumberFormatter.formatCompact(post.likesCount)
                shareButton.text = NumberFormatter.formatCompact(post.sharesCount)
                viewsButton.text = NumberFormatter.formatCompact(post.viewsCount)

                // Устанавливаем иконку лайка и цвет
                if (post.likedByMe) {
                    likeButton.setIconResource(R.drawable.ic_liked_24)
                    likeButton.iconTint = likeButton.context.getColorStateList(R.color.red)
                } else {
                    likeButton.setIconResource(R.drawable.ic_like_24)
                    likeButton.iconTint = likeButton.context.getColorStateList(R.color.material_on_surface_emphasis_medium)
                }

                // Обработка видео
                if (!post.video.isNullOrBlank()) {
                    videoContainer.visibility = View.VISIBLE
                    videoContainer.setOnClickListener {
                        onInteractionListener.onVideoClick(post)
                    }
                    playButton.setOnClickListener {
                        onInteractionListener.onVideoClick(post)
                    }
                    videoPreview.setOnClickListener {
                        onInteractionListener.onVideoClick(post)
                    }
                    likeButton.constraintParams.topToBottom = videoContainer.id
                } else {
                    videoContainer.visibility = View.GONE
                    likeButton.constraintParams.topToBottom = content.id
                }

                // Обработчики событий
                likeButton.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                shareButton.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                // Меню с тремя точками
                menu.setOnClickListener { view ->
                    PopupMenu(view.context, view).apply {
                        menuInflater.inflate(R.menu.post_actions, this.menu)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }
                                R.id.delete -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                else -> false
                            }
                        }
                        show()
                    }
                }
            }
        }

        private val android.view.View.constraintParams: androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            get() = layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
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
        return oldItem.content == newItem.content &&
                oldItem.video == newItem.video &&
                oldItem.likedByMe == newItem.likedByMe &&
                oldItem.likesCount == newItem.likesCount &&
                oldItem.sharesCount == newItem.sharesCount &&
                oldItem.viewsCount == newItem.viewsCount
    }
}
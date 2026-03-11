package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
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

                // Загрузка аватара с сервера
                val avatarUrl = post.authorAvatar?.let {
                    "http://10.0.2.2:9999/avatars/$it"
                }

                Glide.with(avatar.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.netology_avatar)
                    .error(R.drawable.netology_avatar)
                    .transform(CircleCrop())
                    .into(avatar)

                // Форматирование чисел
                likeButton.text = NumberFormatter.formatCompact(post.likes)
                shareButton.text = NumberFormatter.formatCompact(post.shares)
                viewsButton.text = NumberFormatter.formatCompact(post.views)

                // Установка иконки лайка
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
                } else {
                    videoContainer.visibility = View.GONE
                }

                // Обработка вложений
                if (post.attachment != null && post.attachment.type.name == "IMAGE") {
                    attachmentContainer.visibility = View.VISIBLE
                    attachmentDescription.text = post.attachment.description ?: ""

                    val imageUrl = "http://10.0.2.2:9999/images/${post.attachment.url}"
                    Glide.with(attachmentImage.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.netology_avatar)
                        .error(R.drawable.netology_avatar)
                        .centerCrop()
                        .into(attachmentImage)
                } else {
                    attachmentContainer.visibility = View.GONE
                }

                // Обработчики событий
                likeButton.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                shareButton.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                menu.setOnClickListener { view ->
                    PopupMenu(view.context, view).apply {
                        menuInflater.inflate(R.menu.post_actions, menu)
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
                oldItem.likes == newItem.likes &&
                oldItem.shares == newItem.shares &&
                oldItem.views == newItem.views &&
                oldItem.attachment == newItem.attachment &&
                oldItem.authorAvatar == newItem.authorAvatar
    }
}
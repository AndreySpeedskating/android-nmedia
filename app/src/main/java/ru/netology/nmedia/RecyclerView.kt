package ru.netology.nmedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.PostItemBinding


class PostAdapter(
    private val posts: List<Post>,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface OnInteractionListener {
        fun onLike(post: Post)
        fun onShare(post: Post)
        fun onMenu(post: Post)
    }

    inner class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            // Привязка основных данных
            binding.author.text = post.author
            binding.published.text = post.published
            binding.content.text = post.content
            binding.avatar.setImageResource(post.authorAvatar)

            // Установка форматированных значений
            binding.viewsCount.text = NumberFormatter.formatCompact(post.viewsCount)
            binding.shareCount.text = NumberFormatter.formatCompact(post.sharesCount)
            binding.likeCount.text = NumberFormatter.formatCompact(post.likesCount)

            // Установка иконки лайка
            val likeIcon = if (post.likedByMe) {
                R.drawable.ic_liked_24
            } else {
                R.drawable.ic_like_24
            }
            binding.likeButton.setImageResource(likeIcon)

            // ОБРАБОТЧИКИ СОБЫТИЙ (ЗАДАЧА Parent Child)

            // 1. Обработчик на корневом элементе (binding.root)
            binding.root.setOnClickListener {
                android.util.Log.d("ParentChild", "Обработчик root сработал")
                // Здесь можно поставить breakpoint для отладки
            }

            // 2. Обработчик на кнопке лайка
            binding.likeButton.setOnClickListener {
                android.util.Log.d("ParentChild", "Обработчик likeButton сработал")

                // ЛОГИКА ЛАЙКОВ (ЗАДАЧА Like, Share)
                post.likedByMe = !post.likedByMe

                // Обновляем количество лайков
                post.likesCount = if (post.likedByMe) {
                    post.likesCount + 1
                } else {
                    post.likesCount - 1
                }

                // Обновляем UI
                binding.likeCount.text = NumberFormatter.formatCompact(post.likesCount)
                binding.likeButton.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )

                // Уведомляем слушателя
                onInteractionListener.onLike(post)
            }

            // 3. Обработчик на кнопке шеринга
            binding.shareButton.setOnClickListener {
                android.util.Log.d("ParentChild", "Обработчик shareButton сработал")

                // ЛОГИКА ШЕРИНГА (ЗАДАЧА Like, Share)
                post.sharesCount += 1

                // Обновляем UI
                binding.shareCount.text = NumberFormatter.formatCompact(post.sharesCount)

                // Уведомляем слушателя
                onInteractionListener.onShare(post)
            }

            // 4. Обработчик на кнопке меню (три точки)
            binding.menu.setOnClickListener {
                android.util.Log.d("ParentChild", "Обработчик menu сработал")
                onInteractionListener.onMenu(post)
            }

            // 5. Обработчик на аватаре (для тестирования Parent Child)
            binding.avatar.setOnClickListener {
                android.util.Log.d("ParentChild", "Обработчик avatar сработал")
                // Этот обработчик добавим позже для тестирования
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
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}
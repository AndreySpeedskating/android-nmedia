package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L
    private var posts = mutableListOf(
        Post(
            id = nextId++,
            author = "Кто то здесь",
            content = "Инсайдер, известный под ником Debayan Roy, раскрывает дополнительные подробности. По его данным, Realme P4 Power базируется на однокристальной платформе Dimensity 7400, получит экран OLED с диагональю 6,78 дюйма, разрешением 1,5К и кадровой частотой 144 Гц.  В блоке основной камеры пропишутся датчики с разрешением 50, 8 и 2 Мп, разрешение фронтальной камеры составит 16 Мп. Боковая рамка будет пластиковой. Максимальная мощность зарядки составит 80 Вт, масса — 218 граммов. В устройстве будет реализована защита от пыли и воды в соответствии со степенью IP69. Также называется ориентировочная цена — менее 25 тыс. рупий (275 долларов).",
            published = "23 мая в 18:36",
            likedByMe = true,
            likesCount = 125,
            sharesCount = 5,
            viewsCount = 1500,
            avatar = R.drawable.netology_avatar
        ),
        Post(
            id = nextId++,
            author = "Кто то там",
            content = "Задняя крышка смартфона получила двухфактурную отделку: матовый низ сочетается с полупрозрачным верхом. На выбор предложат три цвета: серебристый, оранжевый и синий. Официально сообщается, что устройство будет получать обновления безопасности в течение четырех лет и получит три больших обновления Android.",
            published = "23 мая в 19:00",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 1200,
            viewsCount = 25000,
            avatar = R.drawable.netology_avatar
        ),
        Post(
            id = nextId++,
            author = "Кто то тут",
            content = "Компания Nvidia недавно заявила, что массовое производство ускорителей для ИИ поколения Vera Rubin стартует уже в первом квартале, то есть с опережением сроков. Теперь стало известно, что клиенты получат свои стойки в августе. \n" +
                    "\n" +
                    "Об этом рассказал вице-президент компании Quanta. Вероятно, в августе свои стойки получат далеко не все, но, так или иначе, это дата старта поставок. В случае основных клиентов Nvidia такой запуск обеспечит полную интеграцию уже в четвёртому кварталу этого года. \n" +
                    "\n",
            published = "23 мая в 19:15",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 999,
            viewsCount = 1300000,
            avatar = R.drawable.netology_avatar
        )
    )

    private val _data = MutableStateFlow(posts)
    override val data: Flow<List<Post>> = _data

    override fun getAll(): List<Post> = posts

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                val newLikedByMe = !post.likedByMe
                val newLikesCount = if (newLikedByMe) post.likesCount + 1 else post.likesCount - 1
                post.copyWithLike(newLikedByMe, newLikesCount)
            } else {
                post
            }
        }.toMutableList()
        _data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copyWithShare(post.sharesCount + 1)
            } else {
                post
            }
        }.toMutableList()
        _data.value = posts
    }

    override fun save(post: Post) {

        if (post.id == 0L) {
            posts = (listOf(post.copy(id = nextId++)) + posts).toMutableList()

        } else {
            val index = posts.indexOfFirst { it.id == post.id }

            if (index != -1) {
                posts = posts.map { if (it.id == post.id) post else it }.toMutableList()
            } else {
                return
            }
        }

        _data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }.toMutableList()
        _data.value = posts
    }
}
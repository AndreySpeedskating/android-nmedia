package ru.netology.nmedia.repository

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.activity.NewPostActivity

class NewPostContract : ActivityResultContract<Long?, Unit>() {
    override fun createIntent(context: Context, input: Long?): Intent {
        return Intent(context, NewPostActivity::class.java).apply {
            // Используем elvis-оператор для преобразования nullable в non-null
            putExtra(NewPostActivity.EXTRA_POST_ID, input ?: 0L)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {
        // Просто завершаем метод, ничего не возвращаем
        // Метод возвращает Unit, поэтому return не требуется
    }
}
package ru.netology.nmedia.utils

object NumberFormatter {
    /**
     * Форматирует число в краткую запись (1K, 1.2M и т.д.)
     * Правила:
     * - < 1000: как есть
     * - 1000-9999: с сотнями (1.1K, 6.3K)
     * - 10000-999999: без сотен (10K, 150K)
     * - >= 1000000: с сотнями тысяч (1.3M, 10.1M)
     */
    fun formatCompact(number: Int): String {
        return when {
            number < 1000 -> number.toString()
            number < 10000 -> {
                val hundreds = (number % 1000) / 100
                if (hundreds == 0) {
                    "${number / 1000}K"
                } else {
                    "${number / 1000}.${hundreds}K"
                }
            }
            number < 1000000 -> {
                "${number / 1000}K"
            }
            number < 10000000 -> {
                val hundredsThousands = (number % 1000000) / 100000
                if (hundredsThousands == 0) {
                    "${number / 1000000}M"
                } else {
                    "${number / 1000000}.${hundredsThousands}M"
                }
            }
            else -> {
                "${number / 1000000}M"
            }
        }
    }

    fun testFormatting() {
        val testCases = listOf(
            999 to "999",
            1000 to "1K",
            1125 to "1.1K",
            6399 to "6.3K",
            9999 to "9.9K",
            10000 to "10K",
            150000 to "150K",
            999999 to "999K",
            1300000 to "1.3M",
            10100000 to "10.1M",
            20000000 to "20M"
        )

        testCases.forEach { (input, expected) ->
            val result = formatCompact(input)
            println("$input -> $result (expected: $expected) ${if (result == expected) "✓" else "✗"}")
        }
    }
}
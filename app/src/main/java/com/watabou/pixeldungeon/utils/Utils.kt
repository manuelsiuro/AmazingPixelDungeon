package com.watabou.pixeldungeon.utils
import java.util.Locale
object Utils {
    fun capitalize(str: String): String {
        return str[0].uppercaseChar().toString() + str.substring(1)
    }
    fun format(format: String, vararg args: Any): String {
        return String.format(Locale.ENGLISH, format, *args)
    }
    const val VOWELS = "aoeiu"
    fun indefinite(noun: String): String {
        return if (noun.isEmpty()) {
            "a"
        } else {
            (if (VOWELS.indexOf(noun[0].lowercaseChar()) != -1) "an " else "a ") + noun
        }
    }
}

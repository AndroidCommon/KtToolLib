/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.PrintWriter
import java.io.StringWriter

/**
 * String Utils for common String conversion, transforming, operations and so on.
 */
object StringUtils {

    /**
     * Change str to Int value
     * @param str String to be convert
     * @param default the default value
     * @return the converted value or default if fail
     */
    fun toInt(str: String?, default: Int): Int = str?.toIntOrNull() ?: default

    /**
     * Change str to Float value
     * @param str String to be convert
     * @param default the default value
     * @return the converted value or default if fail
     */
    fun toFloat(str: String?, default: Float): Float = str?.toFloatOrNull() ?: default

    /**
     * Change str to Double value
     * @param str String to be convert
     * @param default the default value
     * @return the converted value or default if fail
     */
    fun toDouble(str: String?, default: Double): Double = str?.toDoubleOrNull() ?: default

    /**
     * Change str to Long value
     * @param str String to be convert
     * @param default the default value
     * @return the converted value or default if fail
     */
    fun toLong(str: String?, default: Long): Long = str?.toLongOrNull() ?: default

    /**
     * extract positive int value from given string text
     * @param str String to extract
     * @param default he default value
     * @return the extracted value or default if fail
     */
    fun extractPositiveInteger(str: String?, default: Int): Int {
        return str?.run {
            var index = 0
            while (index < str.length) { // Search the first digit character
                var curCh = str[index]
                if (curCh in '0'..'9') {
                    val start = index

                    index++ // Search the first non-digit character
                    while (index < str.length) {
                        curCh = str[index]
                        if (curCh in '0'..'9') {
                            index++
                        } else {
                            break
                        }
                    }

                    return try { //May be more than the Integer.MAX_VALUE
                        Integer.parseInt(str.substring(start, index))
                    } catch (e: NumberFormatException) {
                        default
                    }
                }
                index++
            }
            return default
        } ?: default
    }

    // The emoji code point checker
    private fun isEmoJiCharacter(codePoint: Int): Boolean =
        !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)))

    /**
     * check whether the given string value has emoji
     * @param str String to check
     * @return true if contain, or false
     */
    fun containsEmoji(str: String): Boolean = str.takeIf { str.isNotEmpty() }?.run {
        for (ch in str) {
            if (isEmoJiCharacter(ch.toInt())) {
                return true
            }
        }
        return false
    } ?: false

    /**
     * filter emoji from target string, return a new no-emoji string
     * @param str String to filter
     * @return new Filtered string
     */
    fun filterEmoJi(str: String): String = str.takeIf { str.isNotEmpty() }?.run {
        val buf = StringBuilder()
        for (ch in str) {
            if (!isEmoJiCharacter(ch.toInt())) {
                buf.append(ch)
            }
        }
        return buf.toString()
    } ?: str

    /**
     * get Common throwable log output as Text format
     * @param e the throwable to be retrieve
     * @return print StackTrace log
     */
    fun <T : Throwable> getThrowableLog(e: T): String {
        val sw = StringWriter(1024)
        PrintWriter(sw).use {
            e.printStackTrace(it)
        }
        return sw.toString()
    }
}
/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.os.Looper

/**
 * Simple Operation System related Utils
 */
object OsUtils {

    /**
     * Check whether current execution point is inside main thread.
     * @return true if inside main thread, or false
     */
    fun isMainThread(): Boolean = Looper.getMainLooper() == Looper.myLooper()

    /**
     * get current calling stack
     *
     * @return stacktrace text
     */
    fun getCallStack(): String = StringBuilder().apply {
        Thread.currentThread().run {
            this@apply.append("Thread name: ").append(this.name).append('\n')
            for (element in this.stackTrace) {
                this@apply.append("\tat ").append(element.toString()).append('\n')
            }
        }
    }.toString()
}
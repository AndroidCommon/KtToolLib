/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.compat

import android.os.Build
import java.lang.reflect.Method

/**
 * Compat for activityThread related API, not safety cause hidden API.
 */
object ActivityThreadCompat {

    private val CLZ_ACTIVITY_THREAD: Class<*>? by lazy {
        Class.forName("android.app.ActivityThread")
    }
        @Synchronized get

    private val MTD_AT_CURRENT_ACTIVITY_THREAD: Method? by lazy {
        try {
            CLZ_ACTIVITY_THREAD?.getDeclaredMethod("getCurrentActivityThread")?.apply {
                this.isAccessible = true
            }
        } catch (e: Exception) {
            null
        }
    }
        @Synchronized get

    private val MTD_AT_GET_PROCESS_NAME: Method? by lazy {
        try {
            CLZ_ACTIVITY_THREAD?.getDeclaredMethod("getCurrentProcessName")?.apply {
                this.isAccessible = true
            }
        } catch (e: Exception) {
            null
        }
    }
        @Synchronized get

    private val MTD_AT_CURRENT_PROCESS_NAME: Method? by lazy {
        try {
            CLZ_ACTIVITY_THREAD?.getDeclaredMethod("currentProcessName")?.apply {
                this.isAccessible = true
            }
        } catch (e: Exception) {
            null
        }
    }
        @Synchronized get

    /**
     * get current activity thread, better not call above android P
     * @return ActivityThread Object or null
     */
    fun getCurrentActivityThread(): Any? = try {
        MTD_AT_CURRENT_ACTIVITY_THREAD?.invoke(null)
    } catch (e: Exception) {
        null
    }

    /**
     * get current process name, not suggest to use, ref[ta.android.kt.common.utils.ProcessUtils.getCurrentProcessName]
     */
    @Deprecated("Call ta.android.kt.common.utils.ProcessUtils.getCurrentProcessName Instead")
    internal fun getCurrentProcessName(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                MTD_AT_CURRENT_PROCESS_NAME?.invoke(null)?.toString()
            } catch (e: Exception) {
                null
            }?.let { return@getCurrentProcessName it }
        }
        return getCurrentActivityThread()?.run {
            try {
                MTD_AT_GET_PROCESS_NAME?.invoke(this)?.toString()
            } catch (e: Exception) {
                null
            }
        }
    }
}
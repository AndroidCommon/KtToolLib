/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningServiceInfo
import android.content.Context
import java.util.*

/**
 * Android related manager Utils(Like packageManager, activityManager and so on)<br/>
 * Seal common utils operation..
 */
object SysManagerUtils {

    fun getRunningServices(context: Context, maxNum: Int): List<RunningServiceInfo> = try {
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            maxNum
        )
    } catch (e: Exception) {
        Collections.emptyList<RunningServiceInfo>()
    }
}
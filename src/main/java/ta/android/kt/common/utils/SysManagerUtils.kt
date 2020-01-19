/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningServiceInfo
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.util.*

/**
 * Android related manager Utils(Like packageManager, activityManager and so on)<br/>
 * Seal common utils operation..
 */
object SysManagerUtils {

    /**
     * get current app ActivityManager from Context
     * @param context the App Context
     * @return ActivityManager or null if fail
     */
    fun getActivityManager(context: Context): ActivityManager? = try {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    } catch (e: Exception) {
        null
    }

    /**
     * get current running Services in APP
     * @param context the App Context
     * @param maxNum the max limit amount of services
     * @return ServiceInfo List, or empty list if fail
     */
    fun getRunningServices(context: Context, maxNum: Int): List<RunningServiceInfo> =
        getActivityManager(context)?.getRunningServices(maxNum)
            ?: Collections.emptyList<RunningServiceInfo>()


    /**
     * get current running Services in APP
     * @param context the App Context
     * @return ServiceInfo List, or empty list if fail
     */
    fun getRunningServices(context: Context): List<RunningServiceInfo> =
        getRunningServices(context, Int.MAX_VALUE)

    /**
     * get current running processInfo in APP
     * @param context the App Context
     * @return ProcessInfo List, or empty list if fail
     */
    fun getRunningAppProcesses(context: Context): List<RunningAppProcessInfo> =
        getActivityManager(context)?.runningAppProcesses
            ?: Collections.emptyList<RunningAppProcessInfo>()

    /**
     * get current app PackageManager from Context
     * @param context the App Context
     * @return PackageManager or null if fail
     */
    fun getPackageManager(context: Context): PackageManager? = try {
        context.packageManager
    } catch (e: Exception) {
        null
    }

    /**
     * get current install apps PackageInfo list on the mobile
     * @param context the App Context
     * @param flags 0 or [PackageManager.GET_ACTIVITIES], [PackageManager.GET_SERVICES]...
     */
    fun getInstalledPackages(context: Context, flags: Int): List<PackageInfo> =
        getPackageManager(context)?.getInstalledPackages(flags)
            ?: Collections.emptyList<PackageInfo>()
}
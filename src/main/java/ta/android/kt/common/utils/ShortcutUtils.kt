/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat

/**
 * Common shortcut API tools.
 */
object ShortcutUtils {

    private const val ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT"
    private const val ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT"
    private const val EXTRA_SHORTCUT_DUPLICATE = "duplicate"

    private const val UNINSTALL_SHORTCUT_PERMISSION =
        "com.android.launcher.permission.UNINSTALL_SHORTCUT"

    // Send Broadcast to create shortcut.
    private fun sendBroadcastCompat(context: Context, intent: Intent, callback: IntentSender?) {
        if (null == callback) {
            context.sendBroadcast(intent)
        } else {
            context.sendOrderedBroadcast(intent, null, object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    try {
                        callback.sendIntent(context, 0, null, null, null)
                    } catch (e: SendIntentException) {
                    }
                }
            }, null, Activity.RESULT_OK, null, null)
        }
    }

    /**
     * Create a pinned shortcut.
     * @param context app Context
     * @param shortcut the target shortcut to create
     * @param duplicate if same, keep duplicate shortcut
     * @param callback creation callback
     * @return true if creation executed
     */
    fun createPinShortcut(
        context: Context,
        shortcut: ShortcutInfoCompat,
        duplicate: Boolean,
        callback: IntentSender?
    ): Boolean =
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 -> ShortcutManagerCompat.requestPinShortcut(
                context,
                shortcut,
                callback
            )
            ShortcutManagerCompat.isRequestPinShortcutSupported(context) -> {
                ShortcutManagerCompat.createShortcutResultIntent(context, shortcut).let {
                    it.putExtra(EXTRA_SHORTCUT_DUPLICATE, duplicate).action =
                        ACTION_INSTALL_SHORTCUT
                    sendBroadcastCompat(context, it, callback)
                }
                true
            }
            else -> false
        }

    /**
     * Remove a pinned shortcut.
     * @param context app Context
     * @param shortcut the target shortcut to remove
     * @param removeAll remove all shortcut
     * @param callback remove callback
     * @return true if remove performed
     */
    fun removePinShortcut(
        context: Context,
        shortcut: ShortcutInfoCompat,
        removeAll: Boolean,
        callback: IntentSender?
    ): Boolean = if (ContextCompat.checkSelfPermission(
            context,
            UNINSTALL_SHORTCUT_PERMISSION
        ) != PackageManager.PERMISSION_GRANTED
    ) false
    else {
        var hasPermission = false
        for (info in context.packageManager.queryBroadcastReceivers(
            Intent(ACTION_UNINSTALL_SHORTCUT), 0
        )) {
            val permission = info.activityInfo.permission
            if (TextUtils.isEmpty(permission) || UNINSTALL_SHORTCUT_PERMISSION == permission) {
                hasPermission = true
                break
            }
        }

        if (hasPermission) {
            ShortcutManagerCompat.createShortcutResultIntent(context, shortcut).let {
                it.putExtra(EXTRA_SHORTCUT_DUPLICATE, removeAll).action = ACTION_UNINSTALL_SHORTCUT
                sendBroadcastCompat(context, it, callback)
            }
            true
        } else false
    }
}
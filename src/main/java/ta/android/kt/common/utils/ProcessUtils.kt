/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.SparseIntArray
import ta.android.kt.common.compat.ActivityThreadCompat
import java.io.File
import java.util.*


/**
 * Utils to get process related information
 */
object ProcessUtils {

    /**
     * get uif from target pid
     *
     * @param pid PROCESS ID
     * @return UID(INT)
     */
    private fun getUidForPid(pid: Int): Int {
        FileUtils.read2String(String.format(Locale.ENGLISH, "/proc/%d/status", pid))?.run {
            for (line in this.split("\n".toRegex()).toTypedArray()) {
                if (line.startsWith("Uid:")) {
                    return@getUidForPid Integer.parseInt(line.trim().split("\\s+".toRegex()).toTypedArray()[1])
                }
            }
        }
        return -1
    }

    /**
     * get snapshot for list of current app process（PID FOR KEY, UID FOR VALUE)
     * @return snapshot mapping
     */
    fun getProcessSnapshot(): SparseIntArray = SparseIntArray().apply map@{
        File("/proc").listFiles()?.apply files@{
            for (f in this@files) {
                if (f.isDirectory) {
                    try {
                        val pid = Integer.parseInt(f.name)
                        val uid = getUidForPid(pid)
                        if (uid >= android.os.Process.FIRST_APPLICATION_UID) {
                            this@map.append(pid, uid)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    /**
     * Check target process is Alive or not
     *
     * @param process Process
     * @return true if alive, otherwise false
     */
    fun isProcessAlive(process: Process): Boolean =
        try {
            process.exitValue()
            false
        } catch (e: IllegalThreadStateException) {
            true
        }

    /**
     * get process name by pid from ActivityManager
     *
     * @param cxt app Context
     * @param pid PID
     * @return process name or null
     */
    fun getProcessNameByActivityManager(cxt: Context, pid: Int): String? {
        for (info in SysManagerUtils.getRunningAppProcesses(cxt)) {
            if (info.pid == pid) {
                return info.processName
            }
        }
        return null
    }

    /**
     * get process name by pid, usually it is directly read from file system of "/proc"
     * *
     * @param pid PID
     * @return  process name or null
     */
    fun getProcessNameByProc(pid: Int): String? = FileUtils.read2String("/proc/$pid/cmdline")?.run {
        this.trim()
    }

    /**
     * get current process Name of context
     *
     * @param cxt app Context
     * @return process name or null
     */
    fun getCurrentProcessName(cxt: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }

        return android.os.Process.myPid().let {
            getProcessNameByActivityManager(cxt, it).takeIf { name ->
                !TextUtils.isEmpty(name)
            } ?: ActivityThreadCompat.getCurrentProcessName().takeIf { name ->
                !name.isNullOrEmpty()
            } ?: getProcessNameByProc(it)
        }
    }
}
/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import java.io.File

/**
 * Common storage utils to access device storage relative information, path and so on.
 */
object StorageUtils {

    private val EXTRA_SDCARD_PATH: String? by lazy {
        getExternalStoragePath()?.run { "$this/external_sd" }
    }

    /**
     * check external storage availability for API [Environment.getExternalStorageState]
     *
     * @return true if available
     */
    fun externalStorageAvailable(): Boolean =
        try {
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        } catch (e: RuntimeException) {
            false
        }

    /**
     * get Internal storage dir, ref /data
     *
     * @return internal path
     */
    fun getInternalStorageDirectory(): String {
        return Environment.getDataDirectory().absolutePath
    }

    /**
     * get internal storage available disk size
     *
     * @return free size of internal disk
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getInternalStorageAvailableSize(): Long = StatFs(getInternalStorageDirectory()).run {
        blockSizeLong * availableBlocksLong
    }

    /**
     * get internal storage total disk size
     *
     * @return total size of internal disk
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getInternalStorageTotalSize(): Long = StatFs(getInternalStorageDirectory()).run {
        blockSizeLong * blockCountLong
    }

    /**
     * get the percent of internal disk usage
     *
     * @return 0 - 100 for %
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntRange(from = 0, to = 100)
    fun getInertalStorageUsedPercent(): Int = getInternalStorageTotalSize().let { total ->
        getInternalStorageAvailableSize().let { free ->
            ((total - free) * 100L / total).toInt()
        }
    }

    /**
     * get the percent of internal disk free
     *
     *  @return 0 - 100 for %
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntRange(from = 0, to = 100)
    fun getInertalStorageFreedPercent(): Int = getInternalStorageTotalSize().let { total ->
        getInternalStorageAvailableSize().let { free ->
            (free * 100L / total).toInt()
        }
    }

    /**
     * get the percent of external disk usage
     *
     *  @return 0 - 100 for %
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntRange(from = 0, to = 100)
    fun getExternalStorageUsedPercent(): Int =
        if (StorageUtils.externalStorageAvailable()) {
            getExternalStorageTotalSize().let { total ->
                getExternalStorageAvailableSize().let { free ->
                    ((total - free) * 100L / total).toInt()
                }
            }
        } else 0

    /**
     * get the percent of external disk free
     *
     *  @return 0 - 100 for %
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntRange(from = 0, to = 100)
    fun getExternalStorageFreedPercent(): Int =
        if (externalStorageAvailable()) {
            getExternalStorageTotalSize().let { total ->
                getExternalStorageAvailableSize().let { free ->
                    (free * 100L / total).toInt()
                }
            }
        } else 0


    /**
     * get external storage path
     *
     * @return external path
     */
    private fun getExternalStoragePath(): String? = try {
        Environment.getExternalStorageDirectory()?.run {
            absolutePath
        }
    } catch (e: Exception) {
        null
    }

    /**
     * get external storage available size
     *
     * @return the available size, maybe 0
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExternalStorageAvailableSize(): Long =
        try {
            var size = 0L
            if (externalStorageAvailable()) {
                getExternalStoragePath()?.run {
                    StatFs(this).run {
                        size = blockSizeLong * availableBlocksLong
                    }
                }
            }
            size
        } catch (e: Exception) {
            0
        }

    /**
     * get external storage total size
     *
     * @return the total size, maybe 0
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExternalStorageTotalSize(): Long =
        try {
            var size = 0L
            if (externalStorageAvailable()) {
                getExternalStoragePath()?.run {
                    StatFs(this).run {
                        size = blockSizeLong * blockCountLong
                    }
                }
            }
            size
        } catch (e: Exception) {
            0
        }


    /**
     * check extra sd card availability
     *
     * @return true if exits
     */
    fun extraSDCardAvailable(): Boolean = EXTRA_SDCARD_PATH?.run { File(this).exists() } ?: false

    /**
     * get external sd card available size
     *
     * @return available size
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExtraSDCardAvailableSize(): Long =
        if (externalStorageAvailable() && null != EXTRA_SDCARD_PATH) {
            StatFs(EXTRA_SDCARD_PATH).run {
                blockSizeLong * availableBlocksLong
            }
        } else 0


    /**
     * get external sd card total size
     *
     * @return total size
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExtraSDCardTotalSize(): Long =
        if (externalStorageAvailable() && null != EXTRA_SDCARD_PATH) {
            StatFs(EXTRA_SDCARD_PATH).run {
                blockSizeLong * blockCountLong
            }
        } else 0
}
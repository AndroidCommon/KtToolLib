/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.roundToInt

/**
 * Cpu Utils, use to get state, information and so on...
 */
object CpuUtils {
    private val FILE_PROC_STAT = "/proc/stat"
    private val FILE_PROC_PROC_STAT = "/proc/%d/stat"

    private val PATH_FIRST_CPU_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/"

    private val FILE_CPU_SCALE_CURRENT = PATH_FIRST_CPU_FREQ + "scaling_cur_freq"
    private val FILE_CPU_SCALE_MIN = PATH_FIRST_CPU_FREQ + "scaling_min_freq"
    private val FILE_CPU_SCALE_MAX = PATH_FIRST_CPU_FREQ + "scaling_max_freq"

    private val FILE_CPU_SCALES = PATH_FIRST_CPU_FREQ + "scaling_available_frequencies"
    private val FILE_CPU_SCALES_BACKUP = PATH_FIRST_CPU_FREQ + "stats/time_in_state"

    private fun retrieveSegments(line: String): Array<String> =
        line.split("\\s+".toRegex()).toTypedArray()

    /**
     * get cpu usage state
     * @return cpu usage state or null if fail
     */
    fun getCpuStat(): CpuStat? = try {
        BufferedReader(FileReader(FILE_PROC_STAT)).use { br ->
            var line: String?
            do {
                line = br.readLine()?.apply {
                    retrieveSegments(this).let {
                        if (it.size > 4 && it[0] == "cpu") {
                            return@getCpuStat CpuStat(
                                StringUtils.toLong(it[1], 0),
                                StringUtils.toLong(it[2], 0),
                                StringUtils.toLong(it[3], 0),
                                StringUtils.toLong(it[4], 0)
                            )
                        }
                    }
                }
            } while (null != line)
            return@getCpuStat null
        }
    } catch (e: Exception) {
        null
    }

    /**
     * get cpu process related status by pid
     * @param pid the process id
     * @return process id related cpu status, null if fail
     */
    fun getProcCpuStat(pid: Int): ProcCpuStat? = try {
        BufferedReader(
            FileReader(
                String.format(
                    Locale.getDefault(),
                    FILE_PROC_PROC_STAT,
                    pid
                )
            )
        ).use { br ->
            var line: String?
            do {
                line = br.readLine().apply {
                    retrieveSegments(this).let {
                        if (it.size > 20) {
                            return@getProcCpuStat ProcCpuStat(
                                pid, it[2][0],
                                StringUtils.toLong(it[13], 0),
                                StringUtils.toLong(it[14], 0)
                            )
                        }
                    }
                }
            } while (null != line)
            return@getProcCpuStat null
        }
    } catch (e: Exception) {
        null
    }

    /**
     * get cpu process related status
     * @param pids process ids
     * @return Arrays of cpu process status
     */
    fun getProcCpuStats(pids: IntArray): Array<ProcCpuStat?> =
        arrayOfNulls<ProcCpuStat>(pids.size).apply {
            for (i in pids.indices) {
                this[i] = getProcCpuStat(pids[i])
            }
        }.filter { null != it }.toTypedArray()

    /**
     * get supported cpu frequency
     * @return a array of supported cpu frequency
     */
    fun getSupportedFreqs(): IntArray {
        var isOneLine = true
        return (FileUtils.read2String(FILE_CPU_SCALES) ?: FileUtils.read2String(
            FILE_CPU_SCALES_BACKUP
        ).apply {
            isOneLine = false
        })?.run {
            val scale = this.split((if (isOneLine) "\\s+" else "\n").toRegex()).toTypedArray()
            return@getSupportedFreqs if (scale.isNotEmpty()) {
                val set = HashSet<Int>(scale.size)
                for (s in scale) {
                    var freq = 0
                    if (isOneLine) {
                        freq = StringUtils.toInt(s, 0)
                    } else {
                        retrieveSegments(s).let {
                            if (it.isNotEmpty()) {
                                freq = StringUtils.toInt(it[0], 0)
                            }
                        }
                    }
                    if (freq >= 100000) {
                        set.add(freq)
                    }
                }
                val arr = IntArray(set.size)
                var index = 0
                for (freq in set) {
                    arr[index++] = freq
                }
                Arrays.sort(arr)
                arr
            } else {
                IntArray(0)
            }
        } ?: IntArray(0)
    }

    /**
     * hz tp mhz conversion
     * @param hz the hz value
     * @return the converted mhz value
     */
    fun toMhz(hz: Int): Int = (hz / 1000.0).roundToInt()

    /**
     * get current cpu frequency
     * @return the frequency value
     */
    fun getCurrentFreq(): Int = StringUtils.toInt(FileUtils.read2String(FILE_CPU_SCALE_CURRENT), 0)

    /**
     * get current cpu min frequency
     * @return the frequency value
     */
    fun getMinScaleFreq(): Int = StringUtils.toInt(FileUtils.read2String(FILE_CPU_SCALE_MIN), 0)

    /**
     * get current cpu max frequency
     * @return the frequency value
     */
    fun getMaxScaleFreq(): Int = StringUtils.toInt(FileUtils.read2String(FILE_CPU_SCALE_MAX), 0)

    class ProcCpuStat constructor(
        var pid: Int,
        var state: Char,
        var uTime: Long,
        var sTime: Long
    ) {

        fun getDeltaTime(old: ProcCpuStat?): Long = if (old != null) {
            uTime + sTime - old.uTime - old.sTime
        } else 0
    }

    class CpuStat constructor(
        var uTime: Long,
        var nTime: Long,
        var sTime: Long,
        var iTime: Long
    ) {
        val total: Long
            get() = uTime + nTime + sTime + iTime

        /**
         * compute the cpu usage
         *
         * @param preStat the previous cpu state
         * @return [0, 1000]
         */
        fun computeCpuUsage(preStat: CpuStat): Int {
            val preTotal = preStat.total
            val curTotal = total
            return if (curTotal == preTotal) {
                0
            } else {
                1000 - ((iTime - preStat.iTime) * 1000 / (curTotal - preTotal)).toInt()
            }
        }
    }
}
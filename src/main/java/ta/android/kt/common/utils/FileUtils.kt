/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.FileInputStream
import java.io.InputStreamReader

object FileUtils {

    fun readAsString(path: String): String {
        InputStreamReader(FileInputStream(path)).use {
            return it.readText()
        }
    }
}
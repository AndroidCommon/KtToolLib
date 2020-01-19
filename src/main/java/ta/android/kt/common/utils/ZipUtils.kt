/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipFile

/**
 * Common Zip File Utils, use to zip, unzip, recognize and zip operation related and so on....
 */
object ZipUtils {

    private fun isValidPath(path: String): Boolean =
        path.isNotEmpty() && !path.contains("../") // path should not contain any parent relative reference.

    /**
     * check if given path is zip file.
     * @param path file path
     * @return is ZipFile[Boolean]
     */
    fun isZipFile(path: String): Boolean = try {
        ZipFile(path).use {
            return true
        }
    } catch (e: IOException) {
        false
    }

    /**
     * check if given path is apk file.
     * @param path file path
     * @return is ApkFile[Boolean]
     */
    fun isApkFile(path: String): Boolean = try {
        ZipFile(path).use {
            return null != it.getEntry("AndroidManifest.xml")
        }
    } catch (e: IOException) {
        false
    }

    /**
     * unzip target zipFile into output location, if filter provided, then match condition file will zip.
     * @param zipFile zipFile
     * @param output the location for zip
     * @param filter the filter condition, maybe null
     * @return is zip success[Boolean]
     */
    fun unzip(zipFile: File, output: File, filter: ((String) -> Boolean)?): Boolean = try {
        ZipFile(zipFile).use {
            it.entries().run {
                while (this.hasMoreElements()) {
                    this.nextElement().takeIf { e -> isValidPath(e.name) }?.let { e ->
                        if (null != filter && !filter(e.name)) {
                            return@let
                        }

                        File(output, e.name).let resolve@{ f ->
                            if (e.isDirectory) {
                                FileUtils.ensureDirectory(f)
                            } else if (f.parentFile?.run { FileUtils.ensureDirectory(this) } == true &&
                                StreamUtils.in2OutStream(
                                    it.getInputStream(e),
                                    FileOutputStream(f),
                                    StreamUtils.CLOSE_TYPE_BOTH
                                )) {
                                return@resolve
                            } else {
                                return@unzip false
                            }
                        }
                    }
                }
            }
        }
        true
    } catch (e: IOException) {
        false
    }
}
/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

    // The zip single file logic...
    @Throws(IOException::class)
    private fun zip(srcFile: File, zipOut: ZipOutputStream, rootPath: String) {
        (rootPath + (if (rootPath.trim().isEmpty()) "" else File.separator) + srcFile.name).run {
            if (srcFile.isDirectory) {
                srcFile.listFiles()?.apply {
                    for (file in this) {
                        zip(file, zipOut, rootPath)
                    }
                }
            } else {
                zipOut.putNextEntry(ZipEntry(rootPath))
                StreamUtils.in2OutStream(
                    FileInputStream(srcFile),
                    zipOut,
                    StreamUtils.CLOSE_TYPE_IN
                )
                zipOut.flush()
                zipOut.closeEntry()
            }
        }
    }

    /**
     * compress a collection of files to zipFile
     * @param fileList list of files to compress
     * @param zipFile target to zipped file
     * @param comment the extra comment to write inside zip file
     */
    fun zip(fileList: Collection<File>, zipFile: File, comment: String?) {
        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { out ->
                for (resFile in fileList) {
                    zip(resFile, out, "")
                }
                if (comment != null) {
                    out.setComment(comment)
                }
            }
        } catch (e: IOException) {
        }
    }

    /**
     * compress a collection of files to zipFile
     * @param fileList list of files to compress
     * @param zipFile target to zipped file
     */
    fun zip(fileList: Collection<File>, zipFile: File) {
        zip(fileList, zipFile, null)
    }

    /**
     * extract entries name from zip file.
     * @param zipFile target zipFile
     * @return list of entries name of zip file.
     */
    fun extractEntriesNames(zipFile: File): ArrayList<String> = ArrayList<String>().apply {
        try {
            ZipFile(zipFile).use { zf ->
                zf.entries().run {
                    while (this.hasMoreElements()) {
                        this.nextElement().name.takeIf {
                            isValidPath(it)
                        }?.run { this@apply.add(this) }
                    }
                }
            }
        } catch (e: IOException) {
        }
    }
}
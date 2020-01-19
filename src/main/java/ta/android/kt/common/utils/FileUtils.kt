/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import java.io.*


/**
 * File Utils to manipulate file io operation...
 */
object FileUtils {

    // The real read as string logic
    private fun read2StringImpl(file: File, charsetName: String?): String =
        charsetName?.run {
            InputStreamReader(FileInputStream(file), charsetName).use {
                it.readText()
            }
        } ?: InputStreamReader(FileInputStream(file)).use {
            return it.readText()
        }

    /**
     * Read String Content from given path
     * @param file the target file to read
     * @return fileContent
     */
    fun read2String(file: File): String? = read2StringImpl(file, null)

    /**
     * Read String Content from given path
     * @param path the file path
     * @return fileContent
     */
    fun read2String(path: String): String? = read2String(File(path))

    /**
     * Read String Content from given file, decode with charsetName
     * @param file the target file to read
     * @param charsetName decoding charset name
     * @return fileContent
     */
    fun read2String(file: File, charsetName: String): String? = read2StringImpl(file, charsetName)

    /**
     * Read String Content from given path, decode with charsetName
     * @param path the file path
     * @param charsetName decoding charset name
     * @return fileContent
     */
    fun read2String(path: String, charsetName: String): String? =
        read2String(File(path), charsetName)

    // The real save string to file logic
    private fun save4StringImpl(text: String, file: File, append: Boolean, charsetName: String?) =
        try {
            charsetName?.run {
                OutputStreamWriter(FileOutputStream(file, append), charsetName).use {
                    it.write(text)
                }
            } ?: OutputStreamWriter(FileOutputStream(file, append)).use {
                it.write(text)
            }
        } catch (e: IOException) {
        }

    /**
     * Save given text into file location
     * @param text text to be save
     * @param file file to be persist
     */
    fun save4String(text: String, file: File) = save4StringImpl(text, file, false, null)

    /**
     * Save given text into given path location
     * @param text text to be save
     * @param path path to be persist
     */
    fun save4String(text: String, path: String) = save4String(text, File(path))

    /**
     * Save given text into file location, text encode with given charsetName
     * @param text text to be save
     * @param file file to be persist
     * @param charsetName decoding charset name
     */
    fun save4String(text: String, file: File, charsetName: String) =
        save4StringImpl(text, file, false, charsetName)

    /**
     * Save given text into given path location, text encode with given charsetName
     * @param text text to be save
     * @param path path to be persist
     * @param charsetName decoding charset name
     */
    fun save4String(text: String, path: String, charsetName: String) =
        save4String(text, File(path), charsetName)

    /**
     * Append given text at the end of file
     * @param text text to be appended
     * @param file file to be appended
     */
    fun append4String(text: String, file: File) = save4StringImpl(text, file, true, null)

    /**
     * Append given text at the end of path file
     * @param text text to be appended
     * @param path path to be appended
     */
    fun append4String(text: String, path: String) = append4String(text, File(path))

    /**
     * Append given text at the end of file, text encode with given charsetName
     * @param text text to be appended
     * @param file file to be appended
     * @param charsetName decoding charset name
     */
    fun append4String(text: String, file: File, charsetName: String) =
        save4StringImpl(text, file, true, charsetName)

    /**
     * Append given text at the end of path file, text encode with given charsetName
     * @param text text to be appended
     * @param path path to be appended
     * @param charsetName decoding charset name
     */
    fun append4String(text: String, path: String, charsetName: String) =
        append4String(text, File(path), charsetName)

    // The real save bytes logic
    private fun save4BytesImpl(bytes: ByteArray, file: File, append: Boolean) = try {
        FileOutputStream(file, append).use {
            it.write(bytes)
        }
    } catch (e: IOException) {
    }

    /**
     * Save given bytes into file location
     * @param bytes bytes to be saved
     * @param file file to be persist
     */
    fun save4Bytes(bytes: ByteArray, file: File) = save4BytesImpl(bytes, file, false)

    /**
     * Save given bytes into file path
     * @param bytes bytes to be saved
     * @param path file to be persist
     */
    fun save4Bytes(bytes: ByteArray, path: String) = save4Bytes(bytes, File(path))

    /**
     * Append given bytes at the end of file
     * @param bytes bytes to be append
     * @param file file to be append
     */
    fun append4Bytes(bytes: ByteArray, file: File) = save4BytesImpl(bytes, file, true)

    /**
     * Append given bytes at the end of file path
     * @param bytes bytes to be append
     * @param path path to be append
     */
    fun append4Bytes(bytes: ByteArray, path: String) = append4Bytes(bytes, File(path))

    /**
     * Guarantee the given file is directory, if not, delete and remake..
     * @param dir to be tested dir
     * @return the directory path is guaranteed
     */
    fun ensureDirectory(dir: File): Boolean =
        dir.exists() && dir.isDirectory || // path exist and is Directory
                dir.delete() && dir.mkdirs() // then delete and recreate path recursively

    /**
     * Guarantee the given path is directory, if not, delete and remake..
     * @param path to be tested file path
     * @return the directory path is guaranteed
     */
    fun ensureDirectory(path: String): Boolean = ensureDirectory(File(path))

    /**
     * Test target file has Extension
     * @param file to retrieve extension
     */
    fun hasExtension(file: File): Boolean =
        file.extension.isNotEmpty() && file.extension.isNotBlank()

    /**
     * Move sourceFile to target location
     * @param sourceFile the File to move
     * @param targetFile the move file location
     * @return true if move success, otherwise false
     */
    fun moveFile(sourceFile: File, targetFile: File): Boolean = try {
        sourceFile.copyTo(targetFile)
        if (sourceFile.isDirectory) sourceFile.deleteRecursively() else sourceFile.delete()
    } catch (e: IOException) {
        false
    }
}
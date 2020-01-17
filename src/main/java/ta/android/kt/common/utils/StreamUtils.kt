/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */
package ta.android.kt.common.utils

import androidx.annotation.IntDef
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Stream Utils to access common stream operation briefly
 */
object StreamUtils {

    private const val DEFAULT_BUFFER_SIZE = 8 * 1024

    const val CLOSE_TYPE_IN = 0x1
    const val CLOSE_TYPE_OUT = 0x2
    const val CLOSE_TYPE_BOTH = CLOSE_TYPE_IN or CLOSE_TYPE_OUT

    /**
     * Common close for closeable stream, try for IOException
     * @param closeable target closeable stream
     */
    fun <T : Closeable?> close(closeable: T) = try {
        closeable?.close()
    } catch (e: IOException) {
    }

    /**
     * Transform data from inputStream to outputStream
     * @param ins InputStream for read
     * @param os OutputStream to write
     * @param bufferSize data transform buffer size
     * @param type determine which type to close, once finish transforming
     * @return true while transforming success, otherwise false
     */
    fun in2OutStream(
        ins: InputStream,
        os: OutputStream,
        bufferSize: Int, @CloseType type: Int
    ): Boolean {
        try {
            val buffer = ByteArray(bufferSize)
            var nextRead: Boolean
            do {
                nextRead = ins.read(buffer).run {
                    if (this > 0) {
                        os.write(buffer, 0, this)
                    }
                    return@run -1 != this
                }
            } while (nextRead)
        } catch (e: IOException) {
            return false
        } finally {
            if (0 != (type and CLOSE_TYPE_IN)) {
                close(ins)
            }
            if (0 != (type and CLOSE_TYPE_OUT)) {
                close(os)
            }
        }
        return true
    }

    /**
     * Transform data from inputStream to outputStream, Use default bufferSize=8 * 1024
     * @param ins InputStream for read
     * @param os OutputStream to write
     * @param type determine which type to close, once finish transforming
     * @return true while transforming success, otherwise false
     */
    fun in2OutStream(ins: InputStream, os: OutputStream, @CloseType type: Int): Boolean =
        in2OutStream(ins, os, DEFAULT_BUFFER_SIZE, type)

    /**
     * Read Bytes from inputStream
     * @param ins InputStream for read
     * @param autoClose should close after reading
     * @return byteArray from inputStream source, null if fail
     */
    fun read2Bytes(ins: InputStream, autoClose: Boolean): ByteArray? = try {
        if (autoClose) ins.use { ins.readBytes() } else ins.readBytes()
    } catch (e: IOException) {
        null
    }


    /**
     * Close Type Annotation
     */
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(CLOSE_TYPE_BOTH, CLOSE_TYPE_IN, CLOSE_TYPE_OUT)
    annotation class CloseType
}
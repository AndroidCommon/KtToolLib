/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.content.*
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Clipboard utils to manipulate clip, paste API
 */
object ClipboardUtils {
    /**
     *  Copy simple text
     *
     * @param context   app context
     * @param label     clipboard label
     * @param plainText copy text
     */
    fun copyPlainText(context: Context, label: CharSequence?, plainText: CharSequence) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.run {
            setPrimaryClip(ClipData.newPlainText(label, plainText))
        }
    }

    /**
     * Copy simple text
     *
     * @param context   app context
     * @param plainText copy text
     */
    fun copyPlainText(context: Context, plainText: CharSequence) {
        copyPlainText(context, null, plainText)
    }

    /**
     * Copy html content
     *
     * @param context    app context
     * @param label     clipboard label
     * @param plainText simple text if html not supported to copy
     * @param html      HTML Script
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun copyHtml(
        context: Context, label: CharSequence,
        plainText: CharSequence,
        html: String
    ) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.run {
            setPrimaryClip(ClipData.newHtmlText(label, plainText, html))
        }
    }

    /**
     * Copy Intent(Use for app)
     *
     * @param context  app context
     * @param label    clipboard label
     * @param intent  INTENT
     */
    fun copyIntent(context: Context, label: CharSequence, intent: Intent) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.run {
            setPrimaryClip(ClipData.newIntent(label, intent))
        }
    }

    /**
     * Copy Uri
     *
     * @param context app context
     * @param label   clipboard label
     * @param uri     URI
     */
    fun copyUrl(context: Context, label: CharSequence, uri: Uri) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.run {
            setPrimaryClip(ClipData.newRawUri(label, uri))
        }
    }

    /**
     * Copy Uri, supply with resolver.
     *
     * @param context   app context
     * @param resolver [ContentResolver]
     * @param label     clipboard label
     * @param uri      URI
     */
    fun copyUrl(
        context: Context, resolver: ContentResolver,
        label: CharSequence,
        uri: Uri
    ) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.run {
            setPrimaryClip(ClipData.newUri(resolver, label, uri))
        }
    }
}
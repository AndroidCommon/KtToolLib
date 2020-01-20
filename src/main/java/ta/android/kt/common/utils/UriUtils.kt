/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.content.Context
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import androidx.core.content.FileProvider
import java.io.File


/**
 * App common sense uri utils.
 */
object UriUtils {

    /**
     * get uri according to file
     *
     * @param context app context
     * @param file   file to uri
     * @return [Uri]
     */
    fun fromFile(context: Context, file: File): Uri =
        if (SDK_INT > M)
            FileProvider.getUriForFile(
                context,
                context.packageName, file
            )
        else
            Uri.fromFile(file)


    /**
     * get uri according to package name
     *
     * @param packageName package to uri
     * @param fragment   end with '#' content
     * @return [Uri]
     */
    fun fromPackage(packageName: String, fragment: String?): Uri =
        fromParts("package", packageName, fragment)

    /**
     * get uri according to package name
     *
     * @param packageName package to uri
     * @return [Uri]
     */
    fun fromPackage(packageName: String): Uri {
        return fromPackage(packageName, null)
    }

    /**
     * get uri according to context
     *
     * @param context app context
     * @return [Uri]
     */
    fun fromPackage(context: Context): Uri {
        return fromPackage(context.getPackageName())
    }

    /**
     * get common telephone number Uri
     *
     * @param phoneNumber phone number
     * @return [Uri]
     */
    fun fromPhoneCall(phoneNumber: String): Uri {
        return Uri.fromParts("tel", phoneNumber, null)
    }
}
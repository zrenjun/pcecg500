package com.net.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.*
import java.util.*

/**
 *
 * java类作用描述
 * zrj 2021/8/12 9:06
 * 更新者 2021/8/12 9:06
 */
object FileUtil {


    /**
     * 递归创建文件夹
     */
    fun createDir(path: String): File? {
        val file = File(path)
        if (file.exists()) return file
        val p = file.parentFile
        if (!p.exists()) {
            createDir(p.path)
        }
        return if (file.mkdir()) file else null
    }

    /**
     * 递归创建文件夹
     */
    @Throws(IOException::class)
    fun createFile(path: String, fileName: String): File? {
        val filePath = createDir(path)
        if (filePath != null) {
            val file = File(filePath, fileName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            return file
        }
        return null
    }

    /**
     * 递归删除目录
     */
    fun delDir(path: String?): Boolean {
        if (path == null || path == "") return false
        val file = File(path)
        if (file.exists() && file.isDirectory) {
            val delFile = file.listFiles()
            if (delFile != null) {
                if (delFile.isNotEmpty()) {
                    for (file2 in delFile) {
                        if (file2.isFile) {
                            delFile(file2)
                        } else if (file2.isDirectory) {
                            delDir(file2.path)
                        }
                    }
                }
                return file.delete()
            }
        }
        return false
    }

    fun delFile(delFile: File?): Boolean {
        return if (delFile != null && delFile.exists()) {
            delFile.delete()
        } else false
    }

    fun copyFile(oldPath: String, newPath: String): String {
        try {
            var byteread: Int
            val oldFile = File(oldPath)
            if (oldFile.exists()) { //文件存在时
                val inStream: InputStream = FileInputStream(oldPath) //读入原文件
                val fs = FileOutputStream(newPath)
                val buffer = ByteArray(1444)
                while (inStream.read(buffer).also { byteread = it } != -1) {
                    fs.write(buffer, 0, byteread)
                }
                inStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return newPath
    }


    fun readFile(path: String?): String {
        val sb = StringBuilder()
        var fd: FileReader? = null
        var br: BufferedReader? = null
        try {
            fd = FileReader(path)
            br = BufferedReader(fd)
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
                fd?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }


    interface OnCopyProgress {
        fun progress(progress: Long)
    }


    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            file.listFiles().forEach {
                size = if (it.isDirectory) {
                    size + getFolderSize(it)
                } else {
                    size + it.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(file: File?, context: Context): Boolean {
        if (file == null) {
            return false
        }
        return if (file.exists()) {
            true
        } else isFileExists(file.absolutePath, context)
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    private fun isFileExists(filePath: String, context: Context): Boolean {
        val file = getFileByPath(filePath) ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath, context)
    }

    /**
     * Android 10判断文件是否存在的方法
     *
     * @param filePath 文件路径
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    private fun isFileExistsApi29(filePath: String?, context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            var afd: AssetFileDescriptor? = null
            try {
                val uri = Uri.parse(filePath)
                afd = openAssetFileDescriptor(uri, context)
                if (afd == null) {
                    return false
                } else {
                    closeIOQuietly(afd)
                }
            } catch (e: FileNotFoundException) {
                return false
            } finally {
                closeIOQuietly(afd)
            }
            return true
        }
        return false
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) {
            return true
        }
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    @Throws(FileNotFoundException::class)
    fun openAssetFileDescriptor(uri: Uri, context: Context): AssetFileDescriptor? {
        return context.contentResolver.openAssetFileDescriptor(uri, "r")
    }

    private fun closeIOQuietly(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }


    fun isXiaoMi() = checkManufacturer("xiaomi")

    fun isOppo() = checkManufacturer("oppo")

    fun isVivo() = checkManufacturer("vivo")

    private fun checkManufacturer(manufacturer: String) =
        manufacturer.equals(Build.MANUFACTURER, true)

    fun shareFile(context: Context,shareFile: File, type: String = "image/jpeg"){
        context.apply {
            if ((isXiaoMi() || isOppo() || isVivo())
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                && !Settings.canDrawOverlays(this)
            ) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                (this as Activity).startActivityForResult(intent, 0)
                return
            }
            LogUtil.e(shareFile.absolutePath)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = type
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.fileprovider", shareFile)
            } else {
                Uri.fromFile(shareFile)
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            try {
                val chooser = Intent.createChooser(intent, "分享")
                val resInfoList: List<ResolveInfo> =
                    this.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    grantUriPermission(
                        packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                startActivity(chooser)
            } catch (e: ActivityNotFoundException) {
            }
        }
    }
}
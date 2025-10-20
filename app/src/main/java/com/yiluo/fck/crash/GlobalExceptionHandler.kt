package com.yiluo.fck.crash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.os.Process
import org.json.JSONObject
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

class GlobalExceptionHandler(
    private val application: Application
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        handleException(e)
    }

    private fun handleException(e: Throwable) {
        val crashJson = collectCrashInfo(e)
        saveCrashLog(application, crashJson)

        // 跳转到崩溃信息展示页
        val intent = Intent(application, CrashActivity::class.java).apply {
            putExtra("crash_info", crashJson.toString())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)

        // 给 UI 一点时间启动界面
        Thread.sleep(1500)
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    private fun collectCrashInfo(e: Throwable): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val deviceInfo = """
            Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())}
            Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            Android: ${android.os.Build.VERSION.RELEASE} (${android.os.Build.VERSION.SDK_INT})
            Thread: ${Thread.currentThread().name}
        """.trimIndent()

        return "$deviceInfo\n\nStackTrace:\n$stackTrace"
    }

    private fun saveCrashLog(context: Context, content: String) {
        val logDir = File(context.getExternalFilesDir(null), "logs")
        if (!logDir.exists()) logDir.mkdirs()
        val logFile = File(logDir, "crash_${System.currentTimeMillis()}.txt")
        logFile.writeText(content)
    }

    companion object {
        fun init(application: Application) {
            // 1️⃣ 设置全局异常捕获
            val handler = GlobalExceptionHandler(application)
            Thread.setDefaultUncaughtExceptionHandler(handler)

            // 2️⃣ 设置主线程异常循环捕获（补丁 MessageQueue 崩溃问题）
            Thread.setDefaultUncaughtExceptionHandler(handler)

            Thread {
                while (true) {
                    try {
                        Looper.loop() // 启动主线程消息循环
                    } catch (e: Throwable) {
                        // 捕获到 UI 主线程的异常
                        handler.handleException(e)
                        break
                    }
                }
            }.start()
        }
    }
}

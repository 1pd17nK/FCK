package com.yiluo.fck
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.yiluo.fck.crash.GlobalExceptionHandler

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val handler = GlobalExceptionHandler(this)
        Thread.setDefaultUncaughtExceptionHandler(handler)
    }
}
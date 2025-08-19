package com.yiluo.fck.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Singleton

@Singleton
class AppSettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }


    var grade: Int
        get() = prefs.getInt(GRADE, -1)
        set(value) = prefs.edit { putInt(GRADE, value) }


    var subject: Int
        get() = prefs.getInt(SUBJECT, -1)
        set(value) = prefs.edit { putInt(SUBJECT, value) }


    var volume: Int // 分册
        get() = prefs.getInt(VOLUME, -1)
        set(value) = prefs.edit { putInt(VOLUME, value) }

    // 记录一个书名包含记录错题序号，收藏序号，方便保存





    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val GRADE ="grade"
        private const val SUBJECT ="subject"
        private const val VOLUME ="volume"


    }
}
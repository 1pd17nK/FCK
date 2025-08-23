package com.yiluo.fck.ui.screens.select


import androidx.lifecycle.ViewModel
import com.yiluo.fck.data.AppSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SelectViewModel
@Inject constructor(
    private val appSettingsManager: AppSettingsManager
) : ViewModel() {

    val isFirstLaunch: Boolean
        get() = appSettingsManager.isFirstLaunch

    fun setFirstLaunchDone() {
        appSettingsManager.isFirstLaunch = false
    }

    val grade: Int
        get() = appSettingsManager.grade


    val subject: Int
        get() = appSettingsManager.subject

    val volume: Int
        get() = appSettingsManager.volume

    fun setgsv(grade: Int, subject: Int, volume: Int) {
        appSettingsManager.grade = grade
        appSettingsManager.subject = subject
        appSettingsManager.volume = volume
    }


}
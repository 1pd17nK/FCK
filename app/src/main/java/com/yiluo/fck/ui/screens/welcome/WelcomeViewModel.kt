package com.yiluo.fck.ui.screens.welcome


import androidx.lifecycle.ViewModel
import com.yiluo.fck.data.AppSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WelcomeViewModel
@Inject constructor(
    private val appSettingsManager: AppSettingsManager
) : ViewModel() {

}
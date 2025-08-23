package com.yiluo.fck.ui.screens.setting

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yiluo.fck.data.AppSettingsManager
import com.yiluo.fck.ui.theme.ThemeSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

//哇好像很方便，在一个地方统一管理需要context的函数
//用流来管理全局变量，神奇的感觉:))))))

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val application: Application, // Hilt 可以注入 Application Context
    private val themeSettingsManager: ThemeSettingsManager,
    val appPre: AppSettingsManager

) : ViewModel() {


    var currentFile = MutableStateFlow(File("/"))


    val darkTheme = themeSettingsManager.darkTheme

    fun updateDarkTheme(value: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            themeSettingsManager.setDarkTheme(value)
        }


}
package com.yiluo.fck.ui.screens.setting.appearance

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.data.constants.PreferencesConstants
import com.yiluo.fck.ui.anim.AnimatedNavigation
import com.yiluo.fck.ui.components.PreferenceRow
import com.yiluo.fck.ui.components.PreferenceRowSwitch
import com.yiluo.fck.ui.components.ScrollbarLazyColumn
import com.yiluo.fck.ui.screens.setting.AppThemeItem
import com.yiluo.fck.ui.screens.setting.SelectionDialog
import com.yiluo.fck.ui.screens.setting.SettingsScaffoldLazyColumn
import com.yiluo.fck.ui.screens.setting.components.AppThemePreviewItem
import com.yiluo.fck.ui.screens.setting.components.ColorPickerDialog
import com.yiluo.fck.ui.theme.ThemeSettingsManager
import com.yiluo.fck.ui.theme.伐词库Theme

//import com.yiluo.fck.ui.theme.ThemeSettingsManager
//import com.yiluo.fck.ui.theme.期末无挂Theme

@Destination<RootGraph>(style = AnimatedNavigation::class)
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun SettingsAppearanceScreen(
    viewModel: SettingsAppearanceViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

    var darkModeDialog by rememberSaveable { mutableStateOf(false) }
    var paletteStyleDialog by rememberSaveable { mutableStateOf(false) }
    var colorPickerDialog by rememberSaveable { mutableStateOf(false) }

    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle(initialValue = PreferencesConstants.DEFAULT_DARK_THEME)
    val dynamicColors by viewModel.dynamicColors.collectAsStateWithLifecycle(initialValue = PreferencesConstants.DEFAULT_DYNAMIC_COLORS)
    val amoledBlack by viewModel.amoledBlack.collectAsStateWithLifecycle(initialValue = PreferencesConstants.DEFAULT_AMOLED_BLACK)

    val currentPaletteStyle by viewModel.paletteStyle.collectAsStateWithLifecycle(initialValue = PaletteStyle.TonalSpot)
    val currentSeedColor by viewModel.seedColor.collectAsStateWithLifecycle(
        initialValue = Color(
            PreferencesConstants.DEFAULT_THEME_SEED_COLOR
        )
    )
    val isUserDefinedSeedColor by viewModel.isUserDefinedSeedColor.collectAsStateWithLifecycle(
        initialValue = false
    )

    SettingsScaffoldLazyColumn(
        titleText = "主题",
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRow(
                    title = "深色模式",
                    subtitle = when (darkTheme) {
                        0 -> "跟随系统"
                        1 -> "浅色模式"
                        2 -> "深色模式"
                        else -> ""
                    },
                    onClick = { darkModeDialog = true },
                    painter = rememberVectorPainter(Icons.Outlined.DarkMode)
                )
            }

            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "主题颜色"
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        item {
                            伐词库Theme(
                                dynamicColor = true,
                                darkTheme = when (darkTheme) {
                                    0 -> isSystemInDarkTheme()
                                    1 -> false
                                    else -> true
                                },
                                amoled = amoledBlack
                            ) {
                                Column(
                                    modifier = Modifier
                                        .width(115.dp)
                                        .padding(start = 8.dp, end = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AppThemePreviewItem(
                                        selected = dynamicColors,
                                        onClick = {
                                            viewModel.updateDynamicColors(true)
                                            viewModel.updateIsUserDefinedSeedColor(false)
                                        },
                                        colorScheme = MaterialTheme.colorScheme,
                                        shapes = MaterialTheme.shapes
                                    )
                                    Text(
                                        text = "动态颜色",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                    items(
                        listOf(
                            Color.Green to "绿色",
                            Color.Red to "红色",
                            Color.Yellow to "黄色",
                            Color.Blue to "蓝色",
                            Color(0xFFC97820) to "橙色",
                            Color.Cyan to "青色",
                            Color.Magenta to "洋红",
                        )
                    ) {
                        AppThemeItem(
                            title = it.second,
                            colorScheme = rememberDynamicColorScheme(
                                seedColor = it.first,
                                isDark = when (darkTheme) {
                                    0 -> isSystemInDarkTheme()
                                    1 -> false
                                    else -> true
                                },
                                style = currentPaletteStyle,
                                isAmoled = amoledBlack
                            ),
                            onClick = {
                                viewModel.updateDynamicColors(false)
                                viewModel.updateCurrentSeedColor(it.first)
                                viewModel.updateIsUserDefinedSeedColor(false)
                            },
                            selected = currentSeedColor == it.first && !dynamicColors && !isUserDefinedSeedColor,
                            amoledBlack = amoledBlack,
                            darkTheme = darkTheme,
                        )
                    }

                    item {
                        Box {
                            AppThemeItem(
                                title = "自定义",
                                colorScheme = rememberDynamicColorScheme(
                                    seedColor = currentSeedColor,
                                    isDark = when (darkTheme) {
                                        0 -> isSystemInDarkTheme()
                                        1 -> false
                                        else -> true
                                    },
                                    style = currentPaletteStyle,
                                    isAmoled = amoledBlack
                                ),
                                onClick = {
                                    viewModel.updateDynamicColors(false)
                                    viewModel.updateIsUserDefinedSeedColor(true)
                                    colorPickerDialog = true
                                },
                                selected = isUserDefinedSeedColor,
                                amoledBlack = amoledBlack,
                                darkTheme = darkTheme,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 8.dp, end = 16.dp)
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }
            item {
                PreferenceRow(
                    title = "莫奈风格",
                    subtitle = when (currentPaletteStyle) {
                        PaletteStyle.TonalSpot -> "默认"
                        PaletteStyle.Neutral -> "中性"
                        PaletteStyle.Vibrant -> "鲜艳"
                        PaletteStyle.Expressive -> "表现力"
                        PaletteStyle.Rainbow -> "彩虹"
                        PaletteStyle.FruitSalad -> "水果沙拉"
                        PaletteStyle.Monochrome -> "单色"
                        PaletteStyle.Fidelity -> "保真"
                        PaletteStyle.Content -> "内容"
                    },
                    onClick = { paletteStyleDialog = true },
                    painter = rememberVectorPainter(Icons.Outlined.Palette)
                )
            }
            item {
                PreferenceRowSwitch(
                    title = "使用黑色背景",
                    checked = amoledBlack,
                    onClick = {
                        viewModel.updateAmoledBlack(!amoledBlack)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Contrast)
                )
            }
        }
    }

    if (darkModeDialog) {
        SelectionDialog(
            title = "深色模式",
            selections = listOf(
                "跟随系统",
                "浅色模式",
                "深色模式"
            ),
            selected = darkTheme,
            onSelect = { index ->
                viewModel.updateDarkTheme(index)
            },
            onDismiss = { darkModeDialog = false }
        )
    } else if (paletteStyleDialog) {
        SelectionDialog(
            title = "莫奈风格",
            selections = listOf(
                "默认",
                "中性",
                "鲜艳",
                "表现力",
                "彩虹",
                "水果沙拉",
                "单色",
                "保真",
                "内容"
            ),
            selected = ThemeSettingsManager.paletteStyles.find { it.first == currentPaletteStyle }?.second
                ?: 0,
            onSelect = { index ->
                viewModel.updatePaletteStyle(index)
            },
            onDismiss = { paletteStyleDialog = false }
        )
    } else if (colorPickerDialog) {
        val clipboardManager = LocalClipboardManager.current
        var currentColor by remember {
            mutableIntStateOf(currentSeedColor.toArgb())
        }
        ColorPickerDialog(
            currentColor = currentColor,
            onConfirm = {
                viewModel.updateCurrentSeedColor(Color(currentColor))
                colorPickerDialog = false
            },
            onDismiss = {
                colorPickerDialog = false
            },
            onHexColorClick = {
                clipboardManager.setText(
                    AnnotatedString(
                        "#" + currentColor.toHexString(
                            HexFormat.UpperCase
                        )
                    )
                )
            },
            onRandomColorClick = {
                currentColor = (Math.random() * 16777215).toInt() or (0xFF shl 24)
            },
            onColorChange = {
                currentColor = it
            },
            onPaste = {
                val clipboardContent = clipboardManager.getText()
                var parsedColor: Int? = null
                if (clipboardContent != null) {
                    try {
                        parsedColor = clipboardContent.text.toColorInt()
                    } catch (_: Exception) {

                    }
                }
                if (parsedColor != null) {
                    currentColor = parsedColor
                } else {
                    Toast
                        .makeText(
                            context,
                            "不是颜色",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        )
    }

}

package com.yiluo.fck.ui.theme

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

@Composable
fun 伐词库Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    amoled: Boolean = false,
    colorSeed: Color = Color.Blue,
    paletteStyle: PaletteStyle = PaletteStyle.Expressive,
    content: @Composable () -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    
    // 如果开启了动态取色且系统版本支持，虽然可以直接使用系统提供的 scheme，
    // 但系统 scheme 默认风格通常是 TonalSpot，无法应用 Expressive 风格。
    // 如果想要在支持动态取色的同时使用 Expressive 风格，
    // 理想做法是获取壁纸颜色种子然后通过 material-kolor 生成 Expressive 风格的 scheme。
    // 这里为了演示 Expressive 风格，我们在 dynamicColor=false 或者低于 Android 12 时会使用 Expressive。
    // 如果用户非常需要动态取色 + Expressive，可以在这里添加获取壁纸种子的逻辑（需要 API 支持或反射）。
    
    // 目前逻辑：如果不强制使用系统 scheme，则使用 material-kolor 生成（支持 style）。
    // 若 dynamicColor 为 true 且 >= S，我们尝试获取系统 seed？
    // 由于获取系统 seed 比较复杂，这里暂时保留原逻辑，但默认 paletteStyle 改为 Expressive。
    
    var materialThemeState = rememberDynamicMaterialThemeState(
        seedColor = colorSeed,
        isDark = darkTheme,
        style = paletteStyle,
        isAmoled = amoled
    )

    var colorScheme = materialThemeState.colorScheme
    
    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        // 注意：使用系统动态配色会覆盖 paletteStyle 设置，因为系统生成的是固定的 TonalSpot 风格
        colorScheme = when {
            darkTheme && amoled -> dynamicDarkColorScheme(context).copy(
                background = Color.Black,
                surface = Color.Black
            )
            darkTheme && !amoled -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    }

    // 更新状态以反映最终决定的 colorScheme
    materialThemeState = rememberDynamicMaterialThemeState(
        seedColor = colorSeed,
        isDark = darkTheme,
        style = paletteStyle,
        isAmoled = amoled,
        modifyColorScheme = { colorScheme }
    )

    DynamicMaterialTheme(
        state = materialThemeState,
        animate = true,
        animationSpec = tween(
            durationMillis = 300, 
            easing = FastOutSlowInEasing
        ),
        typography = Typography,
        shapes = ExpressiveShapes, // 使用 Expressive 形状
        content = {
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkTheme
                )
            }

            content()
        }
    )
}
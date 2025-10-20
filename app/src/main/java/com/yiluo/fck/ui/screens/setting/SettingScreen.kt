package com.yiluo.fck.ui.screens.setting

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.UpdateDisabled
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsAppearanceScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.ui.anim.AnimatedNavigation
import com.yiluo.fck.ui.components.PreferenceRow
import com.yiluo.fck.ui.components.ScrollbarLazyColumn
import com.yiluo.fck.ui.components.collapsing_topappbar.CollapsingTitle
import com.yiluo.fck.ui.components.collapsing_topappbar.CollapsingTopAppBar
import com.yiluo.fck.ui.components.collapsing_topappbar.rememberTopAppBarScrollBehavior
import com.yiluo.fck.ui.screens.home.HomeViewModel
import com.yiluo.fck.ui.screens.setting.components.AppThemePreviewItem

@Destination<RootGraph>(style = AnimatedNavigation::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel(LocalActivity.current as ComponentActivity)
) {
    var updateModeDialog by rememberSaveable { mutableStateOf(false) }
    var currentUpdate by rememberSaveable { mutableIntStateOf(viewModel.appPre.update) }


    SettingsScaffoldLazyColumn(
        titleText = "设置",
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRow(
                    title = "外观",
                    subtitle = "主题、字体等",
                    onClick = {
                        navigator.navigate(SettingsAppearanceScreenDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Palette)
                )
            }

            item {
                PreferenceRow(
                    title = "立即更新",
                    subtitle = "请不要多次点击",
                    onClick = {
                        homeViewModel.updateRepositoryData()
                    },
                    painter = rememberVectorPainter(Icons.Default.Download)
                )
            }


            item {
                PreferenceRow(
                    title = "自动更新",
                    subtitle = if (currentUpdate == 0) "从不" else "每${currentUpdate}天更新",
                    onClick = { updateModeDialog = true },
                    painter = rememberVectorPainter(if (currentUpdate == 0) Icons.Default.UpdateDisabled else if (currentUpdate == 36500) Icons.Default.QuestionMark else Icons.Default.Update)
                )
            }

            item {
                PreferenceRow(
                    title = "关于",
//                  subtitle = "",
                    onClick = {
                        navigator.navigate(AboutScreenDestination)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Info)
                )
            }
        }

    }

    if (updateModeDialog) {
        SelectionDialog(
            title = "设置更新间隔(天)",
            selections = listOf(
                "从不", "1天", "3天", "一周", "一个月", "一个季", "一年", "一个世纪"
            ),
            selected = when (currentUpdate) {
                0 -> 0
                1 -> 1
                3 -> 2
                7 -> 3
                30 -> 4
                90 -> 5
                365 -> 6
                36500 -> 7
                else -> 0
            },
            onSelect = { index ->
                currentUpdate = when (index) {
                    0 -> 0
                    1 -> 1
                    2 -> 3
                    3 -> 7
                    4 -> 30
                    5 -> 90
                    6 -> 365
                    7 -> 36500
                    else -> 0
                }
                viewModel.appPre.update = currentUpdate

            },
            onDismiss = { updateModeDialog = false }
        )
    }
}


@Composable
fun AppThemeItem(
    title: String,
    colorScheme: ColorScheme,
    amoledBlack: Boolean,
    darkTheme: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(115.dp)
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppThemePreviewItem(
            selected = selected,
            onClick = onClick,
            colorScheme = colorScheme.copy(
                background =
                    if (amoledBlack && (darkTheme == 0 && isSystemInDarkTheme() || darkTheme == 2)) {
                        Color.Black
                    } else {
                        colorScheme.background
                    }
            ),
            shapes = MaterialTheme.shapes
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
    }
}


@Composable
fun SettingsScaffoldLazyColumn(
    navigator: DestinationsNavigator,
    titleText: String,
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = rememberTopAppBarScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(it)
            }
        },
        topBar = {
            CollapsingTopAppBar(
                collapsingTitle = CollapsingTitle.medium(titleText = titleText),
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
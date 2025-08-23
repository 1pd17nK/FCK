package com.yiluo.fck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.materialkolor.PaletteStyle
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AboutLibrariesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QuizScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsAppearanceScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TranScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WelcomeScreenDestination
import com.yiluo.fck.data.AppSettingsManager
import com.yiluo.fck.ui.components.BottomBar
import com.yiluo.fck.ui.theme.ThemeSettingsManager
import com.yiluo.fck.ui.theme.伐词库Theme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@AndroidEntryPoint // Hilt 入口点
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainActivityViewModel = hiltViewModel()

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route


            /// 哈哈，没事就是笑笑
            val dynamicColors by mainViewModel.dc.collectAsStateWithLifecycle(isSystemInDarkTheme())
            val darkTheme by mainViewModel.darkTheme.collectAsStateWithLifecycle(0)
            val amoledBlack by mainViewModel.amoledBlack.collectAsStateWithLifecycle(false)
//            val firstLaunch by mainViewModel.firstLaunch.collectAsStateWithLifecycle(false)
            val colorSeed by mainViewModel.colorSeed.collectAsStateWithLifecycle(initialValue = Color.Red)
            val paletteStyle by mainViewModel.paletteStyle.collectAsStateWithLifecycle(initialValue = PaletteStyle.TonalSpot)
//            val autoUpdateChannel by mainViewModel.autoUpdateChannel.collectAsStateWithLifecycle(UpdateChannel.Disabled)
//            val updateDismissedName by mainViewModel.updateDismissedName.collectAsStateWithLifecycle("")



            伐词库Theme(   darkTheme = when (darkTheme) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            },
                dynamicColor = dynamicColors,
                amoled = amoledBlack,
                colorSeed = colorSeed,
                paletteStyle = paletteStyle
            ) {

                Scaffold(
                    topBar = {
                        val showTopBar = currentRoute in listOf(
                            HomeScreenDestination.route,
                            TranScreenDestination.route,
                            MeScreenDestination.route
                        )
                        if (showTopBar) {
                            TopAppBar(
                                title = {
                                    Text("伐词库")
                                },
                                navigationIcon = {
                                    Icon(
                                        modifier = Modifier.padding(16.dp, 0.dp, 4.dp, 0.dp),
                                        painter = painterResource(R.drawable.logo),
                                        contentDescription = null
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {
//                                        navController.navigate(SettingScreenDestination)
                                        navController.navigate(SettingScreenDestination.route)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))

                                }
                            )
                        }
                        if (currentRoute == QuizScreenDestination.route) {
                            TopAppBar(
                                title = {
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            navController.popBackStack()
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBackIosNew,
                                            contentDescription = null
                                        )
                                    }
                                },
                            )
                        }
                    },
                    bottomBar = {
                        // 根据当前路由判断是否显示 bottomBar
                        val showBottomBar = currentRoute in listOf(
                            HomeScreenDestination.route,
                            TranScreenDestination.route,
                            MeScreenDestination.route
                        )
                        if (showBottomBar) {
                            BottomBar(currentRoute) { route ->
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    if (mainViewModel.isFirstLaunch) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            start = WelcomeScreenDestination,
                            navController = navController, modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            start = HomeScreenDestination,
//                        start = SelectScreenDestination,
                            navController = navController,

                            modifier = if (currentRoute in listOf(
                                    SettingScreenDestination.route,
                                    AboutScreenDestination.route,
                                    SettingsAppearanceScreenDestination.route,
                                    AboutLibrariesScreenDestination.route
                                )
                            ) Modifier else Modifier.padding(innerPadding)
                        )
                    }
                }
//                DestinationsNavHost(
//                    navGraph = NavGraphs.root,
//
//                    navController = navController,
//                )
            }
        }
    }
}


@HiltViewModel
class MainActivityViewModel
@Inject constructor(
    appSettingsManager: AppSettingsManager,
    themeSettingsManager: ThemeSettingsManager,

    ) : ViewModel() {
    val isFirstLaunch = appSettingsManager.isFirstLaunch

    val dc = themeSettingsManager.dynamicColors
    val darkTheme = themeSettingsManager.darkTheme
    val amoledBlack = themeSettingsManager.amoledBlack
    //    val firstLaunch = appSettingsManager.firstLaunch
    val monetSudokuBoard = themeSettingsManager.monetSudokuBoard
    val colorSeed = themeSettingsManager.themeColorSeed
    val paletteStyle = themeSettingsManager.themePaletteStyle
//    val autoUpdateChannel = appSettingsManager.autoUpdateChannel
//    val updateDismissedName = appSettingsManager.updateDismissedName
}
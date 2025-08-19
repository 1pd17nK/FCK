package com.yiluo.fck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QuizScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TranScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WelcomeScreenDestination
import com.yiluo.fck.data.AppSettingsManager
import com.yiluo.fck.ui.components.BottomBar
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

            伐词库Theme(darkTheme = isSystemInDarkTheme()) {

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
                            modifier = Modifier.padding(innerPadding)
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
    appSettingsManager: AppSettingsManager
) : ViewModel() {
    val isFirstLaunch = appSettingsManager.isFirstLaunch

}
package com.yiluo.fck.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TranScreenDestination

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        val items = listOf(
            BottomItem("首页", HomeScreenDestination.route, Icons.Default.Home),
            BottomItem("词典", TranScreenDestination.route, Icons.Default.Book),
            BottomItem("我的", MeScreenDestination.route, Icons.Default.Person)
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomItem(val label: String, val route: String, val icon: ImageVector)

package com.yiluo.fck.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.ui.screens.select.HomeViewModel
import jnu.kulipai.exam.ui.anim.AnimatedNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun MeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(12.dp))



        Text("我的",
            style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(32.dp))

        Row() {
            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    Modifier.padding(16.dp,16.dp,16.dp,8.dp,)
                ) {
                    Text("错题本",
//                        modifier = Modifier.padding(12.dp,12.dp,12.dp,12.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("这里有你答错的所有错题记录，可以反复练习。",
//                        modifier = Modifier.padding(12.dp,12.dp,12.dp,12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    TextButton({}) {
                        Text("去复习")
                    }


                }

            }
            Spacer(Modifier.weight(0.1f))
            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    Modifier.padding(16.dp,16.dp,16.dp,8.dp,)
                ) {
                    Text("收藏本",
//                        modifier = Modifier.padding(12.dp,12.dp,12.dp,12.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("这里有你收藏的记录，可以回顾复习。",
//                        modifier = Modifier.padding(12.dp,12.dp,12.dp,12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    TextButton({}) {
                        Text("去复习")
                    }


                }

            }

        }

    }

}
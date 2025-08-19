package com.yiluo.fck.ui.screens.select

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SelectScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import jnu.kulipai.exam.ui.anim.AnimatedNavigation
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun SelectScreen(
    navigator: DestinationsNavigator,
    viewModel: SelectViewModel = hiltViewModel()
) {

    val numbers = "一二三四"
    val objects = arrayOf("维语精读", "维语听说", "维语阅读")
    val fence = arrayOf("上册", "下册")
    var selectedIndex1 by remember { mutableIntStateOf(-1) }
    var selectedIndex2 by remember { mutableIntStateOf(-1) }
    var selectedIndex3 by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    "${LocalDate.now().monthValue + 1}月后的你将会是",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                Text("我们将为你推荐适合的学习内容")
                Spacer(Modifier.height(24.dp))
                Text(
                    "大学",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    numbers.forEachIndexed { index, ch ->
                        ElevatedFilterChip(index == selectedIndex1, onClick = {
                            selectedIndex1 = index
                        }, label = { Text("大$ch") })
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "科目",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    objects.forEachIndexed { index, ch ->
                        ElevatedFilterChip(index == selectedIndex2, onClick = {
                            selectedIndex2 = index
                        }, label = { Text(ch) })
                    }
                }


                Spacer(Modifier.height(24.dp))
                Text(
                    "分册",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fence.forEachIndexed { index, ch ->
                        ElevatedFilterChip(index == selectedIndex3, onClick = {
                            selectedIndex3 = index
                        }, label = { Text(ch) })
                    }
                }


                Spacer(Modifier.weight(1f))
                FloatingActionButton(

                    modifier = Modifier
                        .padding(
                            0.dp, 0.dp, 0.dp, 64.dp
                        )
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        if (selectedIndex1 != -1 && selectedIndex2 != -1 && selectedIndex3 != -1) {
                            viewModel.setgsv(selectedIndex1, selectedIndex2, selectedIndex3)
                            navigator.navigate(HomeScreenDestination) {
                                popUpTo(SelectScreenDestination) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "未选", Toast.LENGTH_SHORT).show()
                        }
                    },
//
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //全新加载变形等待
                        LoadingIndicator(
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("下一步")
                    }
                }

            }
        }
    }
}
package com.yiluo.fck.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.ui.screens.select.HomeViewModel
import jnu.kulipai.exam.ui.anim.AnimatedNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun TranScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {

        val options = listOf("中->维", "维->中")
        Spacer(Modifier.height(32.dp))


        Text(
            "翻译",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.height(32.dp))


        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = { viewModel.onModeChanged(index) },
                    selected = index == state.mode,
                    label = { Text(label) },
                    modifier = Modifier.width(128.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.originalText,
            onValueChange = {
                viewModel.onOriginalTextChanged(it)
            },
            shape = MaterialTheme.shapes.large,
            label = {
                Text(
                    "请输入文字"
                )
            },

            )
        Spacer(Modifier.height(24.dp))
        FilledTonalIconButton({
            viewModel.translate()
        }, modifier = Modifier.size(48.dp)) {
            Icon(
                Icons.Default.GTranslate,
                contentDescription = null
            )
        }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            isError = state.error.isNotEmpty(),
            supportingText = {
                Text(
                    state.error
                )
            },
            value = if (state.isLoading) "" else state.translatedText,
            onValueChange = {
            },
            shape = MaterialTheme.shapes.large,
            label = {
                Text( if (state.isLoading) "正在翻译..." else "翻译结果")
            }

        )
    }


}
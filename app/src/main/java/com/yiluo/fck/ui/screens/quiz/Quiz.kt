package com.yiluo.fck.ui.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.R
import com.yiluo.fck.ui.anim.AnimatedNavigation
import com.yiluo.fck.ui.screens.home.BookState
import com.yiluo.fck.ui.screens.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun QuizScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    LocalContext.current
    val scope = rememberCoroutineScope()

    // 维语选中文/反过来

    val bookState by viewModel.bookState.collectAsStateWithLifecycle()

    val bookData = (bookState as? BookState.Success)?.bookData
    val bookDataLen = bookData?.length() ?: 0


    fun getBookData(targetQuestion: Int, key: String): String {
        return bookData?.let {
            (bookData[targetQuestion] as JSONObject).get(key)
        }.toString()
    }

    fun randomIntExcluding(x: Int, y: Int, z: Int): Int {
        require(x <= y) { "x 必须小于或等于 y" }
        require(z < x || z > y || (y - x) >= 1) { "区间必须有足够的空间排除 z" }

        var result: Int
        do {
            result = Random.nextInt(x, y + 1) // 注意 nextInt 的上界是开区间，所以要 y+1
        } while (result == z)

        return result
    }


    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()

    var isStar by remember { mutableStateOf(viewModel.isFavorite(currentQuestionIndex)) }

    val isFinish by viewModel.isFinish.collectAsStateWithLifecycle()
    if (bookDataLen != 0)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.popBackStack()
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (isStar) {
                                    isStar = false
                                    viewModel.removeFavorite(currentQuestionIndex)
                                } else {
                                    isStar = true
                                    viewModel.addFavorite(currentQuestionIndex)
                                }
                            }
                        ) {
                            Icon(
                                painter = if (isStar) painterResource(R.drawable.star_fill_24px) else painterResource(
                                    R.drawable.star_24px
                                ),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // 使用 AnimatedContent 添加过渡动画
            AnimatedContent(
                modifier = Modifier.padding(innerPadding),
                targetState = currentQuestionIndex,
                transitionSpec = {
                    // 定义过渡效果，例如：新内容从右侧滑入，旧内容从左侧滑出
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    ).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(durationMillis = 300)
                        )
                    )
                },
                label = "quiz_question_transition"
            ) { targetQuestion ->
                if (!isFinish) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp, 24.dp, 24.dp, 0.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                bookData?.let {
                                    (bookData[targetQuestion] as JSONObject).get("weiyu")
                                }.toString(),
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(Modifier.height(8.dp))

                            Text(
                                bookData?.let {
                                    (bookData[targetQuestion] as JSONObject).get("juzi")
                                }.toString(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(48.dp))
                        val optionss =
                            mutableListOf(
                                currentQuestionIndex,
                                randomIntExcluding(0, bookDataLen - 1, currentQuestionIndex),
                                randomIntExcluding(0, bookDataLen - 1, currentQuestionIndex),
                                randomIntExcluding(0, bookDataLen - 1, currentQuestionIndex)
                            )
                        optionss.shuffle()
                        // 随机答案
                        val options = remember {
                            optionss
                        }
//                    {
//                        listOf(
//                            getBookData(currentQuestionIndex, "dancihanyi"),
//                            getBookData(Random.nextInt(0, bookDataLen), "dancihanyi"),
//                            getBookData(Random.nextInt(0, bookDataLen), "dancihanyi"),
//                            getBookData(Random.nextInt(0, bookDataLen), "dancihanyi"),
//                        ).shuffled()
//                    }

                        var isSelect by remember { mutableStateOf(false) }
                        var isRight by remember { mutableIntStateOf(0) }

                        options.forEach { index ->

                            var answerState by remember { mutableIntStateOf(0) }
                            OutlinedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (answerState) {
                                        0 -> if (isSelect && index == targetQuestion) MaterialTheme.colorScheme.tertiary else Color(
                                            0x00000000
                                        ) // 默认透明
                                        1 -> MaterialTheme.colorScheme.tertiary
                                        2 -> MaterialTheme.colorScheme.error
                                        else -> Color(0x00000000)
                                    }
                                ),
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    if (!isSelect) {
                                        if (index == targetQuestion) {
                                            //正确
                                            answerState = 1
                                            isRight = 1
                                            isSelect = true
                                            scope.launch {
                                                delay(500)
                                                isStar = false
                                                viewModel.nextQuestion()
                                            }

                                        } else {
                                            isSelect = true
                                            answerState = 2
                                            isRight = 2
                                            // 添加错题
                                            viewModel.onWrongAnswer()
                                        }
                                    }
                                }
                            ) {
                                Row {
                                    Text(
                                        text = getBookData(index, "dancihanyi"),
                                        modifier = Modifier.padding(24.dp),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (isSelect) {
                                        Text(
                                            text = getBookData(index, "weiyu"),
                                            modifier = Modifier.padding(0.dp, 24.dp),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    if (isSelect) {
                                        Icon(

                                            if (index == targetQuestion) {
                                                Icons.Default.Check
                                            } else {
                                                Icons.Default.Close
                                            },
                                            modifier = Modifier.padding(24.dp),
                                            contentDescription = null
                                        )

                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))

                        }

                        Spacer(Modifier.weight(1f))
                        FloatingActionButton(

                            modifier = Modifier
                                .padding(
                                    0.dp, 0.dp, 0.dp, 64.dp
                                )
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                when (isRight) {
                                    0 -> "提示"
                                    1 -> "Good!"
                                    2 -> {
                                        viewModel.nextQuestion()
                                        isStar = false
                                    }

                                    else -> ""
                                }
                            }
//
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //全新加载变形等待
                                Icon(
                                    when (isRight) {
                                        0 -> Icons.Default.TipsAndUpdates
                                        1 -> Icons.Default.Check
                                        2 -> Icons.AutoMirrored.Filled.NavigateNext
                                        else -> Icons.Default.TipsAndUpdates
                                    },
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    when (isRight) {
                                        0 -> "提示"
                                        1 -> "Good!"
                                        2 -> "下一题"
                                        else -> ""
                                    }
                                )
                            }
                        }


//                Button({
//                    viewModel.nextQuestion()
//                }) { }
                    }
                } else {  //------finish
                    Text("完成")
                }


            }
        }


}
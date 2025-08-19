package com.yiluo.fck.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.QuizScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.R
import com.yiluo.fck.ui.screens.select.BookState
import com.yiluo.fck.ui.screens.select.HomeViewModel
import com.yiluo.fck.util.log
import jnu.kulipai.exam.ui.anim.AnimatedNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {


    val listData = remember {
        mutableListOf("苹果", "香蕉", "梨子")
    }

    val context = LocalContext.current
    val bookState by viewModel.bookState.collectAsStateWithLifecycle()
    bookState.log()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lib_reference_winter))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = bookState is BookState.Loading,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = bookState is BookState.Loading,
            enter = fadeIn(animationSpec = tween(durationMillis = 100)),
            exit = fadeOut(animationSpec = tween(durationMillis = 50)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,

                ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },

                    )
            }

        }

        AnimatedVisibility(
            visible = bookState is BookState.Success,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 50)),
        ) {

            LazyColumn(
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {

                        Spacer(Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp, 24.dp, 32.dp, 0.dp)
                        ) {

                            Row(modifier = Modifier.fillMaxWidth()) {

                                Image(

                                    painter = painterResource(R.drawable.b1),
                                    contentDescription = null
                                )
                                Column(modifier = Modifier.padding(24.dp, 8.dp)) {
                                    Text(
                                        "维语精读",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(24.dp))

                                    LinearProgressIndicator(
                                        progress = { 0.2f },
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    Text("123/1937")

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Spacer(Modifier.weight(1f))
                                        Button(onClick = {
                                            navigator.navigate(QuizScreenDestination)
                                        }) {
                                            Text("去学习")
                                            Spacer(Modifier.width(4.dp))

                                            Icon(
                                                painterResource(R.drawable.arrow_circle_right_24px),

                                                contentDescription = null
                                            )
                                        }
                                    }

                                }


                            }
                            Spacer(Modifier.height(32.dp))

                            Text(
                                "今日计划",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(24.dp))

                            Row {
                                // card
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        "需学习",
                                        fontSize = 16.sp,
                                    )
                                    Spacer(Modifier.height(32.dp))
                                    Text(
                                        "0/10",
                                        style = MaterialTheme.typography.displaySmall

                                    )

                                }
                                Spacer(Modifier.weight(0.3f))

                                //card
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        "需复习",
                                        fontSize = 16.sp,
                                    )
                                    Spacer(Modifier.height(32.dp))
                                    Text(
                                        "0/10",
                                        style = MaterialTheme.typography.displaySmall
                                    )
                                }

                            }

                            Spacer(Modifier.height(32.dp))
                            Text(
                                "随机词汇",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(32.dp))


                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp, 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Apple",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(2.dp))

                                Text(
                                    "苹果",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }
                            Spacer(Modifier.weight(1f))
                            Icon(
                                painter = painterResource(R.drawable.star_24px),
                                contentDescription = null
                            )
                        }

                    }

                    Spacer(Modifier.height(4.dp))

                }

                items(listData.size) {

                    Card(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        shape = RoundedCornerShape(0.dp)
                    ) {


                        Row(
                            modifier = Modifier.padding(16.dp, 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Apple",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(2.dp))

                                Text(
                                    "苹果",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }
                            Spacer(Modifier.weight(1f))
                            Icon(
                                painter = painterResource(R.drawable.star_24px),
                                contentDescription = null
                            )
                        }


                    }
                    Spacer(Modifier.height(4.dp))

                }
                item {
                    Card(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                    ) {


                        Row(
                            modifier = Modifier.padding(16.dp, 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Apple",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(2.dp))

                                Text(
                                    "苹果",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }
                            Spacer(Modifier.weight(1f))
                            Icon(
                                painter = painterResource(R.drawable.star_24px),
                                contentDescription = null
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(32.dp))


                }

            }
        }
    }
}
package com.yiluo.fck.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SelectScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WelcomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yiluo.fck.R
import com.yiluo.fck.ui.anim.AnimatedNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true, style = AnimatedNavigation::class)
@Composable
fun WelcomeScreen(
    navigator: DestinationsNavigator,
    viewModel: WelcomeViewModel = hiltViewModel()
) {


    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(32.dp, 12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(38.dp),
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "欢迎使用",
                        fontSize = 28.sp,
                    )
                    Text(
                        "伐词库",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(24.dp))

            Text("感谢您使用伐词库!")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "我们深知个人信息的重要性，我们将按照法律法规的要求，采取必要的保护措施确保您个人信息的安全。为更好了解我们如何收集、存储、使用您的个人信息以及您在使用我们产品中的权益，请您务必仔细阅读《用户协议》《隐私政策》及《儿童隐私政策》，其中包括:\n" +
                        "1.我们如何收集和使用您的个人信息，特别是您的个人敏感信息:\n" +
                        "2.我们如何共享、转让、公开、披露您的个人信息;\n" +
                        "3.您对于个人信息享有的权利，包括删除、更改注销等。\n" +
                        "请您阅读上述协议，并确保您在全面了解、知晓协议内容的情况下点击同意。如您代表未成年人做出授权，请您确保您是未成年人的法定监护人。",
                fontSize = 13.sp
            )




            Spacer(Modifier.weight(1F))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,

                ) {
                OutlinedCard(
                    onClick = {
                        throw RuntimeException("666")
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f) // 半透明
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("不同意")
                    }
                }

                Spacer(Modifier.width(12.dp))

                Card(
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f),
                    onClick = {
                        navigator.navigate(SelectScreenDestination) {
                            popUpTo(WelcomeScreenDestination) { inclusive = true }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("同意并进入")
                    }
                }
            }
        }
    }
}
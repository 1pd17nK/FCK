package com.yiluo.fck.crash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.github.kittinunf.fuel.core.FuelManager
import com.materialkolor.PaletteStyle
import com.yiluo.fck.MainActivity
import com.yiluo.fck.ui.theme.伐词库Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val crashInfo = intent.getStringExtra("crash_info") ?: "未知错误"

        setContent {
            val isDark = isSystemInDarkTheme()

            伐词库Theme(
                darkTheme = isDark,
                dynamicColor = false,
                amoled = false,
                colorSeed = Color.Red,
                paletteStyle = PaletteStyle.TonalSpot
            ) {
                CrashScreen(crashInfo = crashInfo, activity = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(crashInfo: String, activity: Activity) {
    val scrollState = rememberScrollState()
    var aiOutput by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("应用崩溃报告") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("应用似乎遇到了问题 🙁", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Text("错误详情：", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                crashInfo.take(1000), // 防止过长崩溃
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { exportLog(activity) }) {
                    Text("导出日志")
                }


            }

            Spacer(Modifier.height(20.dp))
            if (loading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    loading = true
                    aiOutput = ""
                    aiAnalyzeStream(
                        info = crashInfo,
                        onChunk = { chunk ->
                            aiOutput += chunk
                        },
                        onFinish = { loading = false },
                        onError = { err ->
                            aiOutput = err
                            loading = false
                        }
                    )
                }) {
                    Text("AI 分析崩溃原因")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(aiOutput, style = MaterialTheme.typography.bodyLarge)
        }




        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { restartApp(activity) }) {
                Text("重启应用")
            }
            Button(onClick = { activity.finishAffinity() }) {
                Text("关闭应用")
            }
        }
    }
}

private fun exportLog(activity: Activity) {
    val logDir = File(activity.getExternalFilesDir(null), "logs")
    val latest = logDir.listFiles()?.maxByOrNull { it.lastModified() } ?: return
    val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", latest)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    activity.startActivity(Intent.createChooser(shareIntent, "分享崩溃日志"))
}

/**
 * AI 流式分析函数
 * @param info 崩溃日志文本
 * @param onChunk 每次接收到一小段AI生成内容时回调（适合Compose UI实时显示）
 * @param onFinish 最终结束回调
 * @param onError 出错回调
 */
fun aiAnalyzeStream(
    info: String,
    onChunk: (String) -> Unit,
    onFinish: () -> Unit,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // ✅ 增大 Fuel 全局超时配置
            FuelManager.instance.apply {
                timeoutInMillisecond = 120_000
                timeoutReadInMillisecond = 120_000
            }

            val url = URL("https://api.siliconflow.cn/v1/chat/completions")
            val conn = (url.openConnection() as HttpsURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 120_000
                readTimeout = 120_000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty(
                    "Authorization",
                    "Bearer sk-zorqjgfherorpmoargfrhpaqwubzbmvswqlmszonzymqjclo"
                )
                doOutput = true
                doInput = true
            }

            // ✅ 构建 JSON 请求体
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "你是一个专业的Android崩溃日志分析专家。")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", "请分析以下崩溃日志原因并给出修复建议：\n$info")
                })
            }

            val body = JSONObject().apply {
                put("model", "Qwen/Qwen2.5-7B-Instruct")
                put("stream", true) // ✅ 启用流式
                put("temperature", 0.6)
                put("messages", messages)
            }

            conn.outputStream.use { os ->
                os.write(body.toString().toByteArray())
                os.flush()
            }

            // ✅ 按行读取流式响应
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val buffer = StringBuilder()

            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.removePrefix("data: ").trim()
                        if (data == "[DONE]") {
                            onFinish()
                            return@forEach
                        }
                        try {
                            val obj = JSONObject(data)
                            val content = obj
                                .optJSONArray("choices")
                                ?.optJSONObject(0)
                                ?.optJSONObject("delta")
                                ?.optString("content", null)
                            if (!content.isNullOrEmpty()) {
                                buffer.append(content)
                                onChunk(content)
                            }
                        } catch (e: Exception) {
                            Log.w("AIStream", "解析片段失败: $data")
                        }
                    }
                }
            }

            conn.disconnect()
        } catch (e: Exception) {
            Log.e("AIStream", "流式分析错误", e)
            onError("AI 分析异常：${e.message}")
        }
    }
}


private fun restartApp(activity: Activity) {
    val intent = Intent(activity, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    activity.startActivity(intent)
    activity.finish()
    Runtime.getRuntime().exit(0)
}

package com.yiluo.fck.ui.screens.select


import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitUnit
import com.github.kittinunf.fuel.coroutines.awaitString
import com.yiluo.fck.data.AppSettingsManager
import com.yiluo.fck.data.QuizManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject


// 1. 定义一个数据类来表示整个界面的状态
data class TranslationUiState(
    val originalText: String = "",
    val translatedText: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
    val mode: Int = 0
)


sealed class BookState {
    data object Loading : BookState()
    data class Success(val bookData: JSONArray) : BookState()
    data class Error(val message: String) : BookState()
}

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val appSettingsManager: AppSettingsManager,
    private val quizManager: QuizManager,
    private val application: Application, // Hilt 可以注入 Application Context
) : ViewModel() {

    init {
        loadBook(application)
    }

    val grade: Int
        get() = appSettingsManager.grade


    val subject: Int
        get() = appSettingsManager.subject

    val volume: Int
        get() = appSettingsManager.volume

    fun setgsv(grade: Int, subject: Int, volume: Int) {
        appSettingsManager.grade = grade
        appSettingsManager.subject = subject
        appSettingsManager.volume = volume
    }


    // 2. 创建私有的、可变的 StateFlow
    private val _uiState = MutableStateFlow(TranslationUiState())

    // 3. 暴露一个公有的、只读的 StateFlow 供 UI 订阅
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()


    fun onOriginalTextChanged(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(originalText = newText, error = "")
        }
    }

    fun onModeChanged(newMode: Int) {
        _uiState.update { currentState ->
            currentState.copy(mode = newMode, error = "")
        }
    }

    fun translate() {

        viewModelScope.launch {
            // 开始翻译前，进入加载状态
            _uiState.update { it.copy(isLoading = true, error = "") }
            var safeResult = ""
            try {
                val fromLanguage = when (_uiState.value.mode) {
                    0 -> "zh" // 中文
                    1 -> "uy" // 维语
                    else -> "zh" // 默认中文
                }
                val toLanguage = when (_uiState.value.mode) {
                    0 -> "uy" // 翻译到维语
                    1 -> "zh" // 翻译到中文
                    else -> "uy" // 默认翻译到维语
                }
                val jsonString =
                    Fuel.get("https://api.ka721.top/api/niutrans?from=$fromLanguage&to=$toLanguage&mazmun=${_uiState.value.originalText}")
                        .awaitString()

                // 1. 使用原生 JSONObject 解析
                val jsonObject = JSONObject(jsonString)

                safeResult = ""
                if (jsonObject.has("result")) {
                    safeResult = jsonObject.getString("result")
                }

            } catch (e: Exception) { // 包括网络异常和 JSONException
                // 如果发生异常，更新错误状态
                _uiState.update {
                    it.copy(isLoading = false, error = "翻译失败: ${e.message}")
                }
                return@launch
            }


            // 成功后，更新状态
            _uiState.update {
                it.copy(isLoading = false, translatedText = safeResult, error = "")
            }
        }
    }


    val url = "https://gitee.com/qweddcds/daciku/raw/master/"


    private val _bookState = MutableStateFlow<BookState>(BookState.Loading)
    val bookState = _bookState.asStateFlow()


    // 获取书名的函数
    private fun getBookName(grade: Int, subject: Int, volume: Int): String {
        val numbers = "一二三四"
        val objects = arrayOf("维语精读", "维语听说", "维语阅读")
        val fence = arrayOf("上册", "下册")
        return objects[subject] + numbers[grade] + fence[volume]
    }

    // 在 ViewModel 初始化或需要的时候调用此函数
    fun loadBook(context: Context) {

        val bookName = getBookName(grade, subject, volume)

        viewModelScope.launch(Dispatchers.IO) {
            _bookState.value = BookState.Loading

            // 关键：始终使用同一个文件路径
            val destinationFile = File(context.filesDir, "$bookName.json")

            try {
                // 检查缓存是否存在
                if (destinationFile.exists()) {
                    // 缓存命中：直接读取文件
                    val jsonString = destinationFile.readText()
                    _bookState.value = BookState.Success(JSONArray(jsonString))
                } else {
                    // 缓存未命中：从网络下载
                    Fuel.download("$url$bookName.json")
                        .fileDestination { _, _ -> destinationFile }
                        .awaitUnit()

                    // 下载成功后，再次读取同一个文件
                    val jsonString = destinationFile.readText()
                    _bookState.value = BookState.Success(JSONArray(jsonString))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _bookState.value = BookState.Error(e.message ?: "加载失败")
            }
        }
    }

    //------------------------------------Quiz-------------------------------
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()



    // 在答题结束时，导航到结果页
    fun nextQuestion() {
        quizManager.setPos(
            getBookName(grade, subject, volume),
            quizManager.getPos(getBookName(grade, subject, volume)) + 1
        ) // 重置位置
        if (_bookState.value is BookState.Success) {

            if ((_bookState.value as BookState.Success).bookData.length() - 1 <= _currentQuestionIndex.value) {
                // 如果已经是最后一题，设置为完成状态
                _isFinish.value = true
                return
            } else {
                _currentQuestionIndex.value++
            }
        }
    }

    fun onWrongAnswer() {
        quizManager.addWrongQuestion(
            getBookName(grade, subject, volume),
            _currentQuestionIndex.value
        )
    }

    fun onFavoriteAnswer() {
        quizManager.addFavoriteQuestion(
            getBookName(grade, subject, volume),
            _currentQuestionIndex.value
        )
    }

}
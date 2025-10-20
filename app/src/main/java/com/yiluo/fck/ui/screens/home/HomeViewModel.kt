package com.yiluo.fck.ui.screens.home

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import kotlin.math.max
import kotlin.math.min

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
    // 更改 Success 状态，添加 unitIndexMap 以存储 class 到索引的映射
    data class Success(val bookData: JSONArray, val unitIndexMap: Map<Int, Int>) : BookState()
    data class Error(val message: String) : BookState()
}

// 添加单元数据类
data class Unit(
    val id: Int,
    val name: String,
    val description: String = ""
)

val chineseNumbers = arrayOf("", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十")


@HiltViewModel
class HomeViewModel
@Inject constructor(
    val appSettingsManager: AppSettingsManager,
    private val quizManager: QuizManager,
    private val application: Application, // Hilt 可以注入 Application Context
) : ViewModel() {


    private val _showUnitBottomSheet = MutableStateFlow(false)
    val showUnitBottomSheet = _showUnitBottomSheet.asStateFlow()

    // 动态生成的可用单元列表，初始化为空列表
    private val _availableUnits = MutableStateFlow<List<Unit>>(emptyList())
    val availableUnits = _availableUnits.asStateFlow()

    // 存储 class 值到 JSONArray 索引的映射
    private var unitIndexMap: Map<Int, Int> = emptyMap()

    init {
        loadBook(application)
    }

    // ... (chineseNumberToInt, convertToChineseNumber 函数保持不变) ...
    fun chineseNumberToInt(chinese: String): Int {
        val map = mapOf(
            '零' to 0, '一' to 1, '二' to 2, '三' to 3, '四' to 4,
            '五' to 5, '六' to 6, '七' to 7, '八' to 8, '九' to 9
        )

        return when {
            chinese.length == 1 -> {
                // 个位数
                map[chinese[0]] ?: throw IllegalArgumentException("非法汉字数字: $chinese")
            }

            chinese == "十" -> 10
            chinese.startsWith("十") -> {
                // 十几
                10 + (map[chinese[1]] ?: throw IllegalArgumentException("非法汉字数字: $chinese"))
            }

            chinese.endsWith("十") -> {
                // 二十、三十 ...
                (map[chinese[0]] ?: throw IllegalArgumentException("非法汉字数字: $chinese")) * 10
            }

            chinese.contains("十") -> {
                // 二十几、三十几 ...
                val tens =
                    map[chinese[0]] ?: throw IllegalArgumentException("非法汉字数字: $chinese")
                val ones =
                    map[chinese[2]] ?: throw IllegalArgumentException("非法汉字数字: $chinese")
                tens * 10 + ones
            }

            else -> throw IllegalArgumentException("不支持的数字格式: $chinese")
        }
    }


    // 数字转中文数字
    fun convertToChineseNumber(number: Int): String {

        return when {
            number <= 10 -> chineseNumbers[number]
            number < 20 -> "十${chineseNumbers[number - 10]}"
            else -> "${chineseNumbers[number / 10]}十${if (number % 10 == 0) "" else chineseNumbers[number % 10]}"
        }
    }


    // 显示单元选择底部弹出
    fun showUnitSelection() {
        _showUnitBottomSheet.value = true
    }

    // 隐藏单元选择底部弹出
    fun hideUnitSelection() {
        _showUnitBottomSheet.value = false
    }

    /* 旧的 finddancihanyi 函数已移除，逻辑整合到 loadBook 和 selectUnit 中 */

    fun getSelectedUnit(): Int {
        return appSettingsManager.currentUnit
    }

    /**
     * 选择单元。使用 unitIndexMap 来获取 class 对应的起始索引。
     */
    fun selectUnit(unitId: Int) {
        // 1. 更新当前选中的单元ID
        appSettingsManager.currentUnit = unitId

        // 2. 查找该单元ID对应的起始索引
        val startIndex = unitIndexMap[unitId]

        if (startIndex != null && startIndex != -1) {
            // 3. 如果找到了索引，则更新 QuizManager 的位置
            quizManager.setPos(
                getBookName(grade, subject, volume),
                startIndex
            )
            Log.d("HomeViewModel", "Selected Unit $unitId, set quiz position to $startIndex")
        } else {
            Log.w("HomeViewModel", "Unit ID $unitId not found in unitIndexMap.")
            // 如果未找到，可以考虑设置为 0 或给出提示，这里暂时不做处理，保持原有 quizManager.getPos()
        }

        // 确保 UI 上的 Quiz 状态也更新
        _currentQuestionIndex.value = getPos()
        _isFinish.value = false
    }

    // ... (grade, subject, volume, setgsv, onOriginalTextChanged, onModeChanged, translate 保持不变) ...

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

    /**
     * 核心修改：加载书籍数据后，同时解析 class 值，找出最大 class 并构建 unitIndexMap。
     */
    fun loadBook(context: Context) {

        val bookName = getBookName(grade, subject, volume)

        viewModelScope.launch(Dispatchers.IO) {
            _bookState.value = BookState.Loading

            val destinationFile = File(context.filesDir, "$bookName.json")

            try {
                val jsonString = if (destinationFile.exists()) {
                    destinationFile.readText()
                } else {
                    // 缓存未命中：从网络下载
                    Fuel.download("$url$bookName.json")
                        .fileDestination { _, _ -> destinationFile }
                        .awaitUnit()
                    destinationFile.readText()
                }

                val bookData = JSONArray(jsonString)
                var maxUnit = 0
                var minUnit = Int.MAX_VALUE
                val newUnitIndexMap = mutableMapOf<Int, Int>()

                // 遍历 JSONArray 来找到最大 class 并记录每个 class 第一次出现的索引
                for (i in 0 until bookData.length()) {
                    val item = bookData.getJSONObject(i)
                    // 检查是否存在 "class" 字段
                    if (item.has("class")) {
                        try {
                            // 尝试将 class 值（字符串）转换为整数
                            val unitId = item.getString("class").toInt()
                            maxUnit = max(maxUnit, unitId)
                            minUnit = min(minUnit, unitId)


                            // 记录该 unitId 第一次出现的索引
                            if (!newUnitIndexMap.containsKey(unitId)) {
                                newUnitIndexMap[unitId] = i
                            }
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Error parsing class value at index $i: ${e.message}")
                            // 忽略无法解析的 class
                        }
                    }
                }

                // 更新状态和单元列表
                unitIndexMap = newUnitIndexMap.toMap() // 更新 ViewModel 级别的映射
                _bookState.value = BookState.Success(bookData, unitIndexMap)

                // 根据最大单元数动态生成 _availableUnits
                _availableUnits.value = (minUnit..maxUnit).map { unitNumber ->
                    Unit(
                        id = unitNumber,
                        name = "第${convertToChineseNumber(unitNumber)}单元",
                        description = "单元 $unitNumber 学习内容"
                    )
                }

                // 确保加载成功后，Quiz 位置也更新
                _currentQuestionIndex.value = getPos()

            } catch (e: Exception) {
                e.printStackTrace()
                _bookState.value = BookState.Error(e.message ?: "加载失败")
            }
        }
    }


    fun updateRepositoryData() {
        viewModelScope.launch {
            val bookName = getBookName(grade, subject, volume)

            _bookState.value = BookState.Loading
            try {
                // 删除文件，强制重新下载
                File(application.filesDir, "$bookName.json").delete()

                // 重新加载书籍，会触发下载和解析
                loadBook(application)
                appSettingsManager.day = System.currentTimeMillis()
                Toast.makeText(application, "数据已更新！", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(application, "更新失败: ${e.message}", Toast.LENGTH_SHORT).show()
                _bookState.value = BookState.Error(e.message ?: "更新失败")
            }
        }
    }

    //------------------------------------Quiz-------------------------------
    private val _currentQuestionIndex = MutableStateFlow(getPos())
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()


    fun getPos(): Int {
        return quizManager.getPos(getBookName(grade, subject, volume))
    }

    fun getTodayCount(): Int {
        return quizManager.getTodayCount()
    }

    /**
     * nextQuestion 逻辑需要调整，不再通过匹配汉字数字来判断单元切换，而是直接增加索引。
     * 只有在 **selectUnit** 时才设置位置。
     */
    fun nextQuestion() {
        quizManager.increaseTodayCount()

        // 1. 获取当前书本数据和长度
        val bookDataLength = if (_bookState.value is BookState.Success) {
            ((_bookState.value) as BookState.Success).bookData.length()
        } else {
            return // 如果数据未加载成功，直接返回
        }

        // 2. 计算下一个位置
        val nextPos = quizManager.getPos(getBookName(grade, subject, volume)) + 1

        // 3. 判断是否结束
        if (nextPos >= bookDataLength) {
            // 如果到达或超过了最后一题，设置为完成状态
            _isFinish.value = true
            // 可以选择将 Pos 设置回 0 或保持在末尾
            // quizManager.setPos(getBookName(grade, subject, volume), nextPos)
            return
        }

        // 4. 更新 QuizManager 的位置
        quizManager.setPos(getBookName(grade, subject, volume), nextPos)

        // 5. 更新 UI 状态
        _currentQuestionIndex.value = nextPos
        _isFinish.value = false
    }

    // ... (onWrongAnswer, onFavoriteAnswer, addFavorite, removeFavorite, getWrongQuestions,
    // getFavoriteQuestions, removeWrongQuestion, isFavorite 函数保持不变) ...

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

    fun addFavorite(questionId: Int) {
        quizManager.addFavoriteQuestion(
            getBookName(grade, subject, volume),
            questionId
        )
    }

    fun removeFavorite(questionId: Int) {
        quizManager.removeFavoriteQuestion(
            getBookName(grade, subject, volume),
            questionId
        )
    }

    fun getWrongQuestions(): List<Int> {
        return quizManager.getWrongQuestionsList(getBookName(grade, subject, volume))
    }

    fun getFavoriteQuestions(): List<Int> {
        return quizManager.getFavoriteQuestionsList(getBookName(grade, subject, volume))
    }

    fun removeWrongQuestion(questionId: Int) {
        quizManager.removeWrongQuestion(
            getBookName(grade, subject, volume),
            questionId
        )
    }

    fun isFavorite(questionId: Int): Boolean {
        for (i in 0 until getFavoriteQuestions().size) {
            if (getFavoriteQuestions()[i] == questionId) {
                return true
            }
        }
        return false
    }

}
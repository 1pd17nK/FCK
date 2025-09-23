package com.yiluo.fck.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Singleton

@Singleton
class QuizManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("QuizData", Context.MODE_PRIVATE)
    }


    fun addWrongQuestion(bookName: String, questionId: Int) {
        val currentList = getWrongQuestions(bookName).toMutableList()
        if (!currentList.contains(questionId)) {
            currentList.add(questionId)
            saveWrongQuestions(bookName, currentList)
        }
    }


    fun removeWrongQuestion(bookName: String, questionId: Int) {
        val currentList = getWrongQuestions(bookName).toMutableList()
        if (currentList.remove(questionId)) { // remove() 会返回 true/false
            saveWrongQuestions(bookName, currentList)
        }
    }


    fun addFavoriteQuestion(bookName: String, questionId: Int) {
        val currentList = getFavoriteQuestions(bookName).toMutableList()
        if (!currentList.contains(questionId)) {
            currentList.add(questionId)
            saveFavoriteQuestions(bookName, currentList)
        }
    }

    fun removeFavoriteQuestion(bookName: String, questionId: Int) {
        val currentList = getFavoriteQuestions(bookName).toMutableList()
        if (currentList.remove(questionId)) {
            saveFavoriteQuestions(bookName, currentList)
        }
    }

    fun setPos(bookName: String, pos: Int) {
        sharedPreferences.edit {
            putInt("${bookName}_pos", pos)
        }
    }

    fun getPos(bookName: String): Int {
        return sharedPreferences.getInt("${bookName}_pos", 0)

    }

    // --- 内部数据读写方法（原有的） ---


    private fun saveWrongQuestions(bookName: String, questionIds: List<Int>) {
        sharedPreferences.edit {
            putString("${bookName}_wrong_questions", questionIds.joinToString(","))
        }
    }

    private fun getWrongQuestions(bookName: String): List<Int> {
        val idString = sharedPreferences.getString("${bookName}_wrong_questions", null)
        return idString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }

    private fun saveFavoriteQuestions(bookName: String, questionIds: List<Int>) {
        sharedPreferences.edit {
            putString("${bookName}_favorite_questions", questionIds.joinToString(","))
        }
    }

    private fun getFavoriteQuestions(bookName: String): List<Int> {
        val idString = sharedPreferences.getString("${bookName}_favorite_questions", null)
        return idString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }


    // ========== 今日做题数相关 ==========
    private val KEY_TODAY_COUNT = "today_count"
    private val KEY_LAST_DATE = "last_date"

    // ========== 公共方法：获取错题和收藏数据 ==========

    /** 获取指定书籍的错题列表 */
    fun getWrongQuestionsList(bookName: String): List<Int> {
        return getWrongQuestions(bookName)
    }

    /** 获取指定书籍的收藏列表 */
    fun getFavoriteQuestionsList(bookName: String): List<Int> {
        return getFavoriteQuestions(bookName)
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /** 获取今日做题数（会检查日期，若跨天则自动清零） */
    fun getTodayCount(): Int {
        val lastDate = sharedPreferences.getString(KEY_LAST_DATE, null)
        val today = getTodayDate()
        if (lastDate != today) {
            // 新的一天，清零
            resetTodayCount(today)
        }
        return sharedPreferences.getInt(KEY_TODAY_COUNT, 0)
    }

    /** 答对/答错题目时调用 +1 */
    fun increaseTodayCount() {
        val lastDate = sharedPreferences.getString(KEY_LAST_DATE, null)
        val today = getTodayDate()
        if (lastDate != today) {
            resetTodayCount(today)
        }
        val newCount = sharedPreferences.getInt(KEY_TODAY_COUNT, 0) + 1
        sharedPreferences.edit {
            putInt(KEY_TODAY_COUNT, newCount)
            putString(KEY_LAST_DATE, today)
        }
    }

    /** 手动清零 */
    private fun resetTodayCount(today: String) {
        sharedPreferences.edit {
            putInt(KEY_TODAY_COUNT, 0)
            putString(KEY_LAST_DATE, today)
        }
    }

}
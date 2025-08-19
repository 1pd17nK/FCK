package com.yiluo.fck.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
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
}
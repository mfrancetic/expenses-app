package com.mfrancetic.expensesapp.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mfrancetic.expensesapp.models.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ExpensePreferences(
    private val context: Context
) {

    private companion object {
        val Context.dataStore by preferencesDataStore("preferences")
        val EXPENSES = stringPreferencesKey("key_expenses")
    }

    private val itemType = object : TypeToken<List<Expense>>() {}.type

    suspend fun saveExpense(expense: Expense) {
        context.dataStore.edit { preferences ->
            val expenses = fetchExpenses().first()?.toMutableList() ?: mutableListOf()
            expenses.add(expense)
            preferences[EXPENSES] = Gson().toJson(expenses, itemType)
        }
    }

    fun fetchExpenses(): Flow<List<Expense>?> = context.dataStore.data
        .catch { exception ->
            emit(emptyPreferences())
            Log.e(ExpensePreferences::class.java.name, "Exception while fetching expenses: $exception")
        }
        .map { preferences ->
            Gson().fromJson(preferences[EXPENSES], itemType)
        }
}
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

class ExpensesDataStore(
    private val context: Context
) {

    private companion object {
        val Context.dataStore by preferencesDataStore("preferences")
        val EXPENSES = stringPreferencesKey("key_expenses")
    }

    private val gson: Gson = Gson()
    private val itemType = object : TypeToken<List<Expense>>() {}.type

    // region Public Interface

    suspend fun saveExpense(expense: Expense) {
        context.dataStore.edit { preferences ->
            var expenses = fetchExpenses().first()?.toMutableList() ?: mutableListOf()

            expenses = expenses.map { expenseItem ->
                if (expenseItem.id == expense.id) {
                    expense
                } else {
                    expenseItem
                }
            }.toMutableList()

            if (!expenses.contains(expense)){
                expenses.add(expense)
            }

            preferences[EXPENSES] = gson.toJson(expenses, itemType)
        }
    }

    suspend fun deleteExpense(expense: Expense): Boolean {
        val expenses = fetchExpenses().first()?.toMutableList() ?: mutableListOf()
        val isExpenseDeleted = expenses.remove(expense)

        context.dataStore.edit { preferences ->
            preferences[EXPENSES] = gson.toJson(expenses, itemType)
        }

        return isExpenseDeleted
    }

    fun fetchExpenses(): Flow<List<Expense>?> = context.dataStore.data
        .catch { exception ->
            emit(emptyPreferences())
            Log.e(
                ExpensesDataStore::class.java.name,
                "Exception while fetching expenses: $exception"
            )
        }
        .map { preferences ->
            gson.fromJson(preferences[EXPENSES], itemType)
        }


    // endregion
}
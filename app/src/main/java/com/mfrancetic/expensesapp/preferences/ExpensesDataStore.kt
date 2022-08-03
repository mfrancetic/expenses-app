package com.mfrancetic.expensesapp.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mfrancetic.expensesapp.models.DateRange
import com.mfrancetic.expensesapp.models.SortMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ExpensesDataStore(
    private val context: Context
) {

    private companion object {
        val Context.dataStore by preferencesDataStore("preferences")
        val SORT_MODE = stringPreferencesKey("key_sort_mode")
    }

    // region Public Interface

    suspend fun updateSortMode(sortMode: SortMode) {
        context.dataStore.edit { preferences ->
            preferences[SORT_MODE] = sortMode.name
        }
    }

    fun fetchSortMode(): Flow<SortMode?> = context.dataStore.data
        .catch { exception ->
            emit(emptyPreferences())
            Log.e(
                ExpensesDataStore::class.java.name,
                "Exception while fetching expenses: $exception"
            )
        }
        .map { preferences ->
            preferences[SORT_MODE]?.let { sortModeValue ->
                SortMode.valueOf(sortModeValue)
            }
        }

    // endregion
}
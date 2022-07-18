package com.mfrancetic.expensesapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfrancetic.expensesapp.models.ExpensesListState
import com.mfrancetic.expensesapp.preferences.ExpensePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesListViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel(),
    ContainerHost<ExpensesListState, Unit> {

    private val expensePreferences = ExpensePreferences(context)

    override val container =
        container<ExpensesListState, Unit>(ExpensesListState(expenses = emptyList()))

    init {
        fetchExpenses()
    }

    // region Private Helper Methods

    private fun fetchExpenses() = intent {
        viewModelScope.launch {
            expensePreferences.fetchExpenses().collect { expenses ->
                reduce {
                    state.copy(expenses = expenses ?: emptyList())
                }
            }
        }
    }

    // endregion

}
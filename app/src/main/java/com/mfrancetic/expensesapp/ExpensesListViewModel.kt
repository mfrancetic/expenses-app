package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.models.ExpensesListState
import com.mfrancetic.expensesapp.models.SortMode
import com.mfrancetic.expensesapp.preferences.ExpensesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesListViewModel @Inject constructor(
    private val expensesDataStore: ExpensesDataStore
) : ViewModel(),
    ContainerHost<ExpensesListState, ExpensesListSideEffect> {

    override val container =
        container<ExpensesListState, ExpensesListSideEffect>(ExpensesListState(expenses = emptyList()))

    private var sortMode: SortMode = SortMode.ExpenseDateDescending

    init {
        fetchSortMode()
        fetchExpenses()
    }

    // region Public Interface

    fun deleteExpense(expense: Expense) = intent {
        viewModelScope.launch {
            val isExpenseDeleted = expensesDataStore.deleteExpense(expense)

            postSideEffect(
                if (isExpenseDeleted)
                    ExpensesListSideEffect.DisplayExpensesDeletedSuccess
                else ExpensesListSideEffect.DisplayExpensesDeletedFailure
            )
        }
    }

    fun updateSortMode(newSortMode: SortMode) = intent {
        viewModelScope.launch {
            expensesDataStore.updateSortMode(newSortMode)
        }
    }

    // endregion

    // region Private Helper Methods

    private fun fetchExpenses() = intent {
        viewModelScope.launch {
            expensesDataStore.fetchExpenses().collect { expenses ->
                reduce {
                    state.copy(expenses = expenses?.sorted() ?: emptyList())
                }
            }
        }
    }

    private fun fetchSortMode() = intent {
        viewModelScope.launch {
            expensesDataStore.fetchSortMode().collect { newSortMode ->
                if (newSortMode != null && newSortMode != sortMode) {
                    sortMode = newSortMode

                    reduce {
                        state.copy(
                            expenses = state.expenses.sorted()
                        )
                    }
                }
            }
        }
    }

    private fun List<Expense>.sorted(): List<Expense> {
        return if (sortMode == SortMode.ExpenseDateDescending)
            this.sortedByDescending { it.date } else this.sortedBy { it.date }
    }

    // endregion

}
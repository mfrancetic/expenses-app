package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.db.ExpensesAppDatabase
import com.mfrancetic.expensesapp.models.DateRange
import com.mfrancetic.expensesapp.models.DownloadFormat
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.models.ExpensesListState
import com.mfrancetic.expensesapp.models.SortMode
import com.mfrancetic.expensesapp.preferences.ExpensesDataStore
import com.mfrancetic.expensesapp.utils.ExportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesListViewModel @Inject constructor(
    private val expensesDataStore: ExpensesDataStore,
    private val expenseRepository: ExpenseRepository,
    private val expensesAppDatabase: ExpensesAppDatabase,
    private val exportManager: ExportManager
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
        val isExpenseDeleted = expenseRepository.deleteExpense(expense)

        postSideEffect(
            if (isExpenseDeleted)
                ExpensesListSideEffect.DisplayExpenseDeletedSuccess
            else ExpensesListSideEffect.DisplayExpenseDeletedFailure
        )
    }

    fun updateSortMode(newSortMode: SortMode) = intent {
        viewModelScope.launch {
            expensesDataStore.updateSortMode(newSortMode)
        }
    }

    fun updateDateRange(newDateRange: DateRange) {
        fetchExpenses(newDateRange)
    }

    fun removeDateRange(){
        fetchExpenses()
    }

    fun downloadData(downloadFormat: DownloadFormat) = intent {
        val isDatabaseExported = exportManager.exportDatabase(expensesAppDatabase, downloadFormat)

        postSideEffect(
            if (isDatabaseExported) ExpensesListSideEffect.DisplayExpensesDataDownloadSuccess
            else ExpensesListSideEffect.DisplayExpensesDataDownloadFailure
        )
    }

    fun deleteAllExpenses() = intent {
        val areAllExpensesDeleted = expenseRepository.deleteAllExpenses()

        postSideEffect(
            if (areAllExpensesDeleted) ExpensesListSideEffect.DisplayExpensesDataDownloadSuccess
            else ExpensesListSideEffect.DisplayExpensesDataDownloadFailure
        )
    }

    // endregion

    // region Private Helper Methods

    private fun fetchExpenses(dateRange: DateRange? = null) = intent {
        viewModelScope.launch {
            expenseRepository.fetchAllExpenses(dateRange).collect { expenses ->
                reduce {
                    state.copy(expenses = expenses.filterDeleted().sorted(), isFilterEnabled = dateRange != null)
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
                            expenses = state.expenses.filterDeleted().sorted()
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

    private fun List<Expense>.filterDeleted(): List<Expense> {
        return this.filter { it.deletionDate == null }
    }


    // endregion

}
package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpensesDetailState
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpensesDetailViewModel @Inject constructor() : ViewModel(),
    ContainerHost<ExpensesDetailState, ExpensesListSideEffect> {

    private val initialExpense: Expense = Expense(
        id = UUID.randomUUID().toString(),
        title = "",
        amount = "",
        category = ExpenseCategory.Rent,
        date = System.currentTimeMillis()
    )

    override val container = container<ExpensesDetailState, ExpensesListSideEffect>(
        ExpensesDetailState(
            expense = initialExpense
        )
    )

    fun onExpenseUpdated(expense: Expense) = intent {
        reduce {
            state.copy(expense = expense, isSaveExpenseEnabled = isExpenseValid(expense))
        }
    }

    fun onSaveButtonClicked(expense: Expense) = intent {
        reduce {
            state.copy(expense = initialExpense)
        }
    }

    private fun isExpenseValid(expense: Expense): Boolean {
        return expense.amount.isNotBlank() &&
                expense.title.isNotBlank() && expense.date != 0L
    }

}
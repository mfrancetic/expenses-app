package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.models.ExpensesDetailSideEffect
import com.mfrancetic.expensesapp.models.ExpensesDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesDetailViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) :
    ViewModel(),
    ContainerHost<ExpensesDetailState, ExpensesDetailSideEffect> {

    override val container = container<ExpensesDetailState, ExpensesDetailSideEffect>(
        ExpensesDetailState(expense = Expense())
    )

    fun initWithExpense(expense: Expense?) = intent {
        reduce {
            state.copy(expense = expense ?: Expense())
        }
    }

    fun onExpenseUpdated(expense: Expense) = intent {
        reduce {
            state.copy(expense = expense, isSaveExpenseEnabled = isExpenseValid(expense))
        }
    }

    fun onSaveButtonClicked(expense: Expense) = intent {
        expenseRepository.addExpense(expense)

        postSideEffect(ExpensesDetailSideEffect.NavigateBack)
    }

    private fun isExpenseValid(expense: Expense): Boolean {
        return expense.amount > 0 &&
                expense.title.isNotBlank() && expense.date != 0L
    }
}
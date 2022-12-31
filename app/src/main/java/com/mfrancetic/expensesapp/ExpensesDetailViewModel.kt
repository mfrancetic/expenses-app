package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.models.AmountError
import com.mfrancetic.expensesapp.models.ExpenseViewData
import com.mfrancetic.expensesapp.models.ExpensesDetailSideEffect
import com.mfrancetic.expensesapp.models.ExpensesDetailState
import com.mfrancetic.expensesapp.models.TitleError
import com.mfrancetic.expensesapp.utils.ExpenseViewDataFactory.toExpense
import com.mfrancetic.expensesapp.utils.ExpenseViewDataFactory.toExpenseViewData
import com.mfrancetic.expensesapp.utils.ValidationConstants.MAX_AMOUNT
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
        ExpensesDetailState(expense = initialExpenseViewData())
    )

    fun initWithExpense(expense: ExpenseViewData?) = intent {
        reduce {
            state.copy(expense = expense ?: initialExpenseViewData())
        }
    }

    fun onExpenseUpdated(expense: ExpenseViewData) = intent {
        reduce {
            state.copy(
                expense = expense, isSaveExpenseEnabled = isExpenseValid(expense),
                hasEditingStarted = true,
                titleError = titleError(expense.title), amountError = amountError(expense.amount)
            )
        }
    }

    fun onSaveButtonClicked(expense: ExpenseViewData) = intent {
        expenseRepository.addExpense(expense.toExpense())

        postSideEffect(ExpensesDetailSideEffect.NavigateBack)
    }

    private fun isExpenseValid(expense: ExpenseViewData): Boolean {
        return amountError(expense.amount) == null &&
                expense.title.isNotBlank() && expense.date != 0L
    }

    private fun titleError(title: String): TitleError? {
        return if (title.isBlank()) {
            TitleError.TitleEmpty
        } else {
            null
        }
    }

    private fun amountError(amount: Double?): AmountError? {
        return if (amount == null) {
            AmountError.AmountEmpty
        } else if (amount == 0.0) {
            AmountError.AmountTooLow
        } else if ((amount.toString().contains(".") &&
                amount.toString().substringAfter(".").length > 2)
            || (amount.toString().contains(",") &&
                    amount.toString().substringAfter(",").length > 2))
        {
            AmountError.InvalidFormat
        } else {
            null
        }
    }

    private fun initialExpenseViewData(): ExpenseViewData {
        return Expense().toExpenseViewData().copy(
            amount = null,
            amountString = ""
        )
    }
}
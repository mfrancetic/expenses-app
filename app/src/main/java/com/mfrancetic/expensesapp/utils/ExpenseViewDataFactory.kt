package com.mfrancetic.expensesapp.utils

import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.models.ExpenseViewData

object ExpenseViewDataFactory {

    fun Expense.toExpenseViewData(): ExpenseViewData {
        return ExpenseViewData(
            id = this.id,
            title = this.title,
            amount = this.amount,
            amountString = this.amount.toString(),
            currency = this.currency,
            category = this.category,
            date = this.date,
            deletionDate = this.deletionDate
        )
    }

    fun ExpenseViewData.toExpense(): Expense {
        return Expense(
            id = this.id,
            title = this.title,
            amount = this.amount ?: 0.0,
            currency = this.currency,
            category = this.category,
            date = this.date,
            deletionDate = this.deletionDate
        )
    }
}
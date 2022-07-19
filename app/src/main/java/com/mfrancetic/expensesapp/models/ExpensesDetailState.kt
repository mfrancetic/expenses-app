package com.mfrancetic.expensesapp.models

import com.mfrancetic.expensesapp.db.Expense

data class ExpensesDetailState(
    val expense: Expense,
    val isSaveExpenseEnabled: Boolean = false
)
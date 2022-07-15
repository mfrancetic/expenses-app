package com.mfrancetic.expensesapp.models

data class ExpensesDetailState(
    val expense: Expense,
    val isSaveExpenseEnabled: Boolean = false
)
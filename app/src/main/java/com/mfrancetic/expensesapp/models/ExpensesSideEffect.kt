package com.mfrancetic.expensesapp.models

sealed class ExpensesSideEffect {
    object NavigateFromExpensesDetailsToExpensesList: ExpensesSideEffect()
}
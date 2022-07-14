package com.mfrancetic.expensesapp.models

sealed class ExpensesSideEffect {
    object NavigateToExpensesDetailsScreen: ExpensesSideEffect()
}
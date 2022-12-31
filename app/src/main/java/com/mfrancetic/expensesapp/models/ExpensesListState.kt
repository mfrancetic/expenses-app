package com.mfrancetic.expensesapp.models

import com.mfrancetic.expensesapp.db.Expense

data class ExpensesListState(
    val expenses: List<Expense>,
    val isFilterEnabled: Boolean = false,
    val isLoading: Boolean = true,
)
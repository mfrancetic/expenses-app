package com.mfrancetic.expensesapp.models

data class ExpensesDetailState(
    val expense: ExpenseViewData,
    val isSaveExpenseEnabled: Boolean = false,
    val hasEditingStarted: Boolean = false,
    val titleError: TitleError? = null,
    val amountError: AmountError? = null
)
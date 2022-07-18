package com.mfrancetic.expensesapp.models

sealed class ExpensesListSideEffect {
    object DisplayExpensesDeletedSuccess : ExpensesListSideEffect()
    object DisplayExpensesDeletedFailure : ExpensesListSideEffect()
}
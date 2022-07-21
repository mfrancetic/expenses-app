package com.mfrancetic.expensesapp.models

sealed class ExpensesListSideEffect {
    object DisplayExpenseDeletedSuccess : ExpensesListSideEffect()
    object DisplayExpenseDeletedFailure : ExpensesListSideEffect()

    object DisplayExpensesDataDownloadSuccess: ExpensesListSideEffect()
    object DisplayExpensesDataDownloadFailure: ExpensesListSideEffect()

    object DisplayAllExpensesDeletedSuccess: ExpensesListSideEffect()
    object DisplayAllExpensesDeletedFailure: ExpensesListSideEffect()
}
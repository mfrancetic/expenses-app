package com.mfrancetic.expensesapp.models

sealed class ExpensesListSideEffect {
    object NavigateBack: ExpensesListSideEffect()
}
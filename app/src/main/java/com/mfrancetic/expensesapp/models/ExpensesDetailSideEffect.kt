package com.mfrancetic.expensesapp.models

sealed class ExpensesDetailSideEffect {
    object NavigateBack : ExpensesDetailSideEffect()
}
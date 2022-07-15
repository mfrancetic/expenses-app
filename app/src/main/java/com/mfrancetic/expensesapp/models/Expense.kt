package com.mfrancetic.expensesapp.models

data class Expense(
    val id: String,
    val title: String,
    val amount: String,
    val category: String,
    val date: String
)
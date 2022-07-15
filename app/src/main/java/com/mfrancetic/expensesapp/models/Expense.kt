package com.mfrancetic.expensesapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val id: String,
    val title: String,
    val amount: String,
    val category: ExpenseCategory,
    val date: String
): Parcelable
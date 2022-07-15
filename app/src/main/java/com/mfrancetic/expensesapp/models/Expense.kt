package com.mfrancetic.expensesapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val id: String,
    var title: String,
    var amount: String,
    var category: ExpenseCategory,
    var date: Long
): Parcelable
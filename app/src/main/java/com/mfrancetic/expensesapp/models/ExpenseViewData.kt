package com.mfrancetic.expensesapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseViewData(
    val id: String, val title: String,
    val amount: Double?,
    val amountString: String,
    val currency: ExpenseCurrency,
    val category: ExpenseCategory, val date: Long,
    val deletionDate: Long?
) : Parcelable


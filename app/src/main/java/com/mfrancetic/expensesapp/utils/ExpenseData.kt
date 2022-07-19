package com.mfrancetic.expensesapp.utils

import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import java.util.*

object ExpenseData {

    val initialExpense: Expense = Expense(
        id = UUID.randomUUID().toString(),
        title = "",
        amount = 0.00,
        category = ExpenseCategory.Rent,
        date = System.currentTimeMillis()
    )
}
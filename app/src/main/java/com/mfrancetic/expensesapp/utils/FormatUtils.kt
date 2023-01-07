package com.mfrancetic.expensesapp.utils

import android.content.Context
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpenseCurrency
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

object FormatUtils {

    fun Double.formatCurrency(currency: ExpenseCurrency): String {
                val locale = when (currency) {
            ExpenseCurrency.HRK -> Locale("hr", "HR")
            ExpenseCurrency.EUR -> Locale("de", "DE")
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
        currencyFormat.apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }

        return currencyFormat.format(this)
    }

    fun ExpenseCategory.name(context: Context): String {
        return when(this){
            ExpenseCategory.Rent -> context.getString(R.string.expense_category_rent)
            ExpenseCategory.Utilities -> context.getString(R.string.expense_category_utilities)
            ExpenseCategory.Groceries -> context.getString(R.string.expense_category_groceries)
            ExpenseCategory.Pharmacy -> context.getString(R.string.expense_category_pharmacy)
            ExpenseCategory.Restaurants -> context.getString(R.string.expense_category_restaurants)
            ExpenseCategory.Entertainment -> context.getString(R.string.expense_category_entertainment)
            ExpenseCategory.Travel -> context.getString(R.string.expense_category_travel)
            ExpenseCategory.Car -> context.getString(R.string.expense_category_car)
            ExpenseCategory.MedicalExpenses -> context.getString(R.string.expense_category_medical_expenses)
            ExpenseCategory.Clothing -> context.getString(R.string.expense_category_clothing)
            ExpenseCategory.Grooming -> context.getString(R.string.expense_category_grooming)
            ExpenseCategory.Gifts -> context.getString(R.string.expense_category_gifts)
            ExpenseCategory.Other -> context.getString(R.string.expense_category_other)
        }
    }
}
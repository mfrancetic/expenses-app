package com.mfrancetic.expensesapp.utils

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
}
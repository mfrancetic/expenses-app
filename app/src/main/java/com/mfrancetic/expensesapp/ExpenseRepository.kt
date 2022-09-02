package com.mfrancetic.expensesapp

import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.db.ExpenseDao
import com.mfrancetic.expensesapp.models.DateRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(private val expenseDao: ExpenseDao) {

    fun fetchAllExpenses(dateRange: DateRange? = null): Flow<List<Expense>> {
        return if (dateRange != null) {
            expenseDao.getExpensesForDateRange(dateRange.startDate.time, dateRange.endDate.time)
        } else {
            expenseDao.getAllExpenses()
        }
    }

    fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun deleteExpense(expense: Expense): Boolean {
        expenseDao.insertExpense(expense.copy(deletionDate = System.currentTimeMillis()))
        return expenseDao.getExpenseById(expense.id).first().deletionDate != null
    }

    suspend fun deleteAllExpenses(): Boolean {
        expenseDao.deleteAllExpenses()
        var areAllExpensesDeleted = false
        expenseDao.getAllExpenses().collect {
            areAllExpensesDeleted = it.isEmpty()
        }
        return areAllExpensesDeleted
    }
}
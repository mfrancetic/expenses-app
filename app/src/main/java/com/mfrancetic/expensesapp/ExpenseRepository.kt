package com.mfrancetic.expensesapp

import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.db.ExpenseDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(private val expenseDao: ExpenseDao) {

    fun fetchAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun deleteExpense(id: String): Boolean {
        expenseDao.deleteExpense(id)
        return expenseDao.getExpenseById(id).isEmpty()
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
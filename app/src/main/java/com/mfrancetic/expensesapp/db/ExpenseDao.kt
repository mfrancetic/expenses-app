package com.mfrancetic.expensesapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    fun getExpenseById(id: String): List<Expense>

    @Insert(onConflict = REPLACE)
    fun insertExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE expenseId = :id")
    fun deleteExpense(id: String)

    @Query("DELETE FROM expenses")
    fun deleteAllExpenses()
}
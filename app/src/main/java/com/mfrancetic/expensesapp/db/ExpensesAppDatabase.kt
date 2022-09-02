package com.mfrancetic.expensesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Expense::class], version = 2)
abstract class ExpensesAppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
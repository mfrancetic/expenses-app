package com.mfrancetic.expensesapp.di.modules

import android.content.Context
import androidx.room.Room
import com.mfrancetic.expensesapp.ExpenseRepository
import com.mfrancetic.expensesapp.db.ExpenseDao
import com.mfrancetic.expensesapp.db.ExpensesAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ExpensesAppDatabase {
        return Room.databaseBuilder(
            appContext,
            ExpensesAppDatabase::class.java,
            "Expenses"
        ).build()
    }

    @Provides
    fun provideExpenseDao(appDatabase: ExpensesAppDatabase): ExpenseDao {
        return appDatabase.expenseDao()
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository =
        ExpenseRepository(expenseDao)
}
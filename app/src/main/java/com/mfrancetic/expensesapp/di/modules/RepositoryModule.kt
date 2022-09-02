package com.mfrancetic.expensesapp.di.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val migrationOneToTwo: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE expenses "
                        + "ADD COLUMN deletionDate INTEGER"
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ExpensesAppDatabase {
        return Room.databaseBuilder(
            appContext,
            ExpensesAppDatabase::class.java,
            "expenses_database"
        ).addMigrations(
            migrationOneToTwo
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE).

        build()
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
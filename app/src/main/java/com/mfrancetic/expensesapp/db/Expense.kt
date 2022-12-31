package com.mfrancetic.expensesapp.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpenseCurrency
import kotlinx.parcelize.Parcelize
import java.util.UUID
import javax.annotation.Nonnull

@Entity(tableName = "expenses")
@Parcelize
data class Expense(

    @PrimaryKey
    @ColumnInfo(name = "expenseId")
    @Nonnull
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "expenseTitle")
    @Nonnull
    var title: String = "",

    @ColumnInfo(name = "expenseAmount")
    @Nonnull
    var amount: Double = 0.0,

    @ColumnInfo(name = "expenseCurrency")
    @Nonnull
    var currency: ExpenseCurrency = ExpenseCurrency.EUR,

    @ColumnInfo(name = "expenseCategory")
    @Nonnull
    var category: ExpenseCategory = ExpenseCategory.Other,

    @ColumnInfo(name = "expenseDate")
    @Nonnull
    var date: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "deletionDate")
    var deletionDate: Long? = null
) : Parcelable
package com.mfrancetic.expensesapp.di.modules

import android.content.Context
import com.mfrancetic.expensesapp.preferences.ExpensesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideExpensesDataStore(@ApplicationContext context: Context) =
        ExpensesDataStore(context)
}
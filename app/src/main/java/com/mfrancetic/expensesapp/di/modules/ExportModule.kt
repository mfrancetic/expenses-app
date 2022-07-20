package com.mfrancetic.expensesapp.di.modules

import android.content.Context
import com.mfrancetic.expensesapp.utils.ExportManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ExportModule {

    @Provides
    @Singleton
    fun provideExportManager(@ApplicationContext appContext: Context): ExportManager =
        ExportManager(appContext)
}
package com.oftatech.superschoolboyremastered.dao

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DaoProviders {

    @Provides
    fun provideMainDAO(@ApplicationContext context: Context): MainSPDao {
        return MainSPDao(context)
    }

    @Provides
    fun provideStatisticsDao(@ApplicationContext context: Context): StatisticsSPDao {
        return StatisticsSPDao(context)
    }
}
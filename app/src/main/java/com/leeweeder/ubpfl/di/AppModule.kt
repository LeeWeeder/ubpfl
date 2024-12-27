package com.leeweeder.ubpfl.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.leeweeder.ubpfl.data.DataStoreRepository
import com.leeweeder.ubpfl.data.DefaultDataStoreRepository
import com.leeweeder.ubpfl.data.source.AppDatabase
import com.leeweeder.ubpfl.feature_progression.data.DefaultProgressionRepository
import com.leeweeder.ubpfl.feature_progression.data.ProgressionRepository
import com.leeweeder.ubpfl.feature_timer.data.DefaultRecentTimerDurationsRepository
import com.leeweeder.ubpfl.feature_timer.data.RecentTimerDurationsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesMyApplicationDatabase(app: Application): AppDatabase {
        return Room
            .databaseBuilder(
                app,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .build()
    }

    @Provides
    @Singleton
    fun providesDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository {
        return DefaultDataStoreRepository(context)
    }

    @Provides
    @Singleton
    fun providesRecentTimerDurationsRepository(
        @ApplicationContext context: Context
    ): RecentTimerDurationsRepository {
        return DefaultRecentTimerDurationsRepository(context)
    }

    @Provides
    @Singleton
    fun providesProgressionRepository(
        db: AppDatabase
    ): ProgressionRepository {
        return DefaultProgressionRepository(db.progressionDao)
    }
}
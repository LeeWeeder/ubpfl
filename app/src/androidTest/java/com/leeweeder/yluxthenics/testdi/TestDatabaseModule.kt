package com.leeweeder.yluxthenics.testdi

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.leeweeder.yluxthenics.data.DataStoreRepository
import com.leeweeder.yluxthenics.data.DefaultDataStoreRepository
import com.leeweeder.yluxthenics.data.source.AppDatabase
import com.leeweeder.yluxthenics.feature_progression.data.DefaultProgressionRepository
import com.leeweeder.yluxthenics.feature_progression.data.ProgressionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideMyApplicationDatabase(app: Application): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            app,
            AppDatabase::class.java,
        ).build()
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
    fun providesProgressionRepository(
        db: AppDatabase
    ): ProgressionRepository {
        return DefaultProgressionRepository(db.progressionDao)
    }

    /*@Provides
    @Singleton
    fun provideProgramRepository(db: AppDatabase): ProgramRepository {
        return ProgramRepositoryImpl(db.programDao)
    }

    @Provides
    @Singleton
    fun provideProgramUseCases(repository: ProgramRepository): ProgramUseCases {
        return ProgramUseCases(
            getPrograms = GetPrograms(repository),
            getProgramById = GetProgramById(repository),
            upsertProgram = UpsertProgram(repository),
            deleteProgram = DeleteProgram(repository)
        )
    }*/
}
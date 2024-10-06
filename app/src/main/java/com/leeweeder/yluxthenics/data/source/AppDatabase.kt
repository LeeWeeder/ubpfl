package com.leeweeder.yluxthenics.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.leeweeder.yluxthenics.feature_progression.data.source.Progression
import com.leeweeder.yluxthenics.feature_progression.data.source.ProgressionDao

@Database(
    entities = [Progression::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract val progressionDao: ProgressionDao

    companion object {
        const val DATABASE_NAME = "luxthenics_db"
    }
}
package com.leeweeder.luxthenics.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.leeweeder.luxthenics.Program
import com.leeweeder.luxthenics.Progression
import com.leeweeder.luxthenics.api_program.asset.ExerciseCategory
import com.leeweeder.luxthenics.api_program.asset.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.InputStream
import java.io.OutputStream

interface DataStoreRepository {
    val programFlow: Flow<Program>
    val progressionFlow: Flow<Progression>

    suspend fun setPeriod(period: Period)

    suspend fun setProgress(value: Int)

    suspend fun setIsInitialized(value: Boolean)

    suspend fun setProgressionLevel(exerciseCategory: ExerciseCategory, level: Int)
}

private val Context.programDataStore: DataStore<Program> by dataStore(
    fileName = "program.pb", serializer = ProgramSerializer
)

private val Context.progressionDataStore: DataStore<Progression> by dataStore(
    fileName = "progression.pb", serializer = ProgressionSerializer
)

class DefaultDataStoreRepository(context: Context) : DataStoreRepository {
    private val programDataStore = context.programDataStore
    private val progressionDataStore = context.progressionDataStore

    override val programFlow: Flow<Program> = programDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(Program.getDefaultInstance())
            } else {
                throw exception
            }
        }
    override val progressionFlow: Flow<Progression> = progressionDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(Progression.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun setPeriod(period: Period) {
        programDataStore.updateData { preferences ->
            preferences.toBuilder().setPeriod(period.ordinal).build()
        }
    }

    override suspend fun setProgress(value: Int) {
        programDataStore.updateData { preferences ->
            preferences.toBuilder().setProgress(value).build()
        }
    }

    override suspend fun setIsInitialized(value: Boolean) {
        progressionDataStore.updateData { preferences ->
            preferences.toBuilder().setIsInitialized(value).build()
        }
    }

    override suspend fun setProgressionLevel(exerciseCategory: ExerciseCategory, level: Int) {
        progressionDataStore.updateData { preferences ->
            val builder = preferences.toBuilder()

            when (exerciseCategory) {
                ExerciseCategory.BentArmDynamic -> builder.setBentArmDynamic(level)
                ExerciseCategory.HorizontalPull -> builder.setHorizontalPull(level)
                ExerciseCategory.HorizontalPush -> builder.setHorizontalPush(level)
                ExerciseCategory.StraightArmDynamic -> builder.setStraightArmDynamic(level)
                ExerciseCategory.SupportedStatic -> builder.setSupportedStatic(level)
                ExerciseCategory.UnsupportedStatic -> builder.setUnsupportedStatic(level)
                ExerciseCategory.VerticalPull -> builder.setVerticalPull(level)
                ExerciseCategory.VerticalPush -> builder.setVerticalPush(level)
                ExerciseCategory.WeightedHorizontalPush -> builder.setWeightedHorizontalPush(level)
            }

            builder.build()
        }
    }
}

object ProgramSerializer : Serializer<Program> {
    override val defaultValue: Program =
        Program.getDefaultInstance().toBuilder().setProgress(0).setPeriod(Period.Start.ordinal)
            .build()

    override suspend fun readFrom(input: InputStream): Program {
        try {
            return Program.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: Program, output: OutputStream) {
        t.writeTo(output)
    }
}

object ProgressionSerializer : Serializer<Progression> {
    override val defaultValue: Progression
        get() = Progression.getDefaultInstance().toBuilder().setBentArmDynamic(1)
            .setHorizontalPull(1).setHorizontalPush(1).setStraightArmDynamic(1)
            .setSupportedStatic(1).setUnsupportedStatic(1).setVerticalPull(1).setVerticalPush(1)
            .setWeightedHorizontalPush(1).build()

    override suspend fun readFrom(input: InputStream): Progression {
        try {
            return Progression.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: Progression, output: OutputStream) {
        t.writeTo(output)
    }
}

fun Progression.getLevelByExerciseCategory(exerciseCategory: ExerciseCategory): Int {
    return when (exerciseCategory) {
        ExerciseCategory.UnsupportedStatic -> this.unsupportedStatic
        ExerciseCategory.SupportedStatic -> this.supportedStatic
        ExerciseCategory.StraightArmDynamic -> this.straightArmDynamic
        ExerciseCategory.BentArmDynamic -> this.bentArmDynamic
        ExerciseCategory.VerticalPull -> this.verticalPull
        ExerciseCategory.HorizontalPull -> this.horizontalPull
        ExerciseCategory.VerticalPush -> this.verticalPush
        ExerciseCategory.HorizontalPush -> this.horizontalPush
        ExerciseCategory.WeightedHorizontalPush -> this.weightedHorizontalPush
    }
}

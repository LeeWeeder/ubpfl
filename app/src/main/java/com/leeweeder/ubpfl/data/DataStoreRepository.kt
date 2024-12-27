package com.leeweeder.ubpfl.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.leeweeder.ubpfl.Program
import com.leeweeder.ubpfl.Progression
import com.leeweeder.ubpfl.api_program.asset.Macrocycle
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.InputStream
import java.io.OutputStream

interface DataStoreRepository {
    val macrocyleFlow: Flow<Program>
    val progressionFlow: Flow<Progression>

    suspend fun setPeriod(period: Macrocycle)

    suspend fun setProgress(value: Int)

    suspend fun setInitialized(value: Boolean)

    suspend fun setProgressionLevel(progressiveExercise: ProgressiveExercise, level: Int)
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

    override val macrocyleFlow: Flow<Program> = programDataStore.data.catch { exception ->
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

    override suspend fun setPeriod(period: Macrocycle) {
        programDataStore.updateData { preferences ->
            preferences.toBuilder().setPeriod(period.ordinal).build()
        }
    }

    override suspend fun setProgress(value: Int) {
        programDataStore.updateData { preferences ->
            preferences.toBuilder().setProgress(value).build()
        }
    }

    override suspend fun setInitialized(value: Boolean) {
        progressionDataStore.updateData { preferences ->
            preferences.toBuilder().setInitialized(value).build()
        }
    }

    override suspend fun setProgressionLevel(progressiveExercise: ProgressiveExercise, level: Int) {
        progressionDataStore.updateData { preferences ->
            val builder = preferences.toBuilder()

            when (progressiveExercise) {
                ProgressiveExercise.BentArmDynamic -> builder.setBentArmDynamic(level)
                ProgressiveExercise.HorizontalPull -> builder.setHorizontalPull(level)
                ProgressiveExercise.HorizontalPush -> builder.setHorizontalPush(level)
                ProgressiveExercise.StraightArmDynamic -> builder.setStraightArmDynamic(level)
                ProgressiveExercise.SupportedStatic -> builder.setSupportedStatic(level)
                ProgressiveExercise.UnsupportedStatic -> builder.setUnsupportedStatic(level)
                ProgressiveExercise.VerticalPull -> builder.setVerticalPull(level)
                ProgressiveExercise.VerticalPush -> builder.setVerticalPush(level)
                ProgressiveExercise.WeightedHorizontalPush -> builder.setWeightedHorizontalPush(level)
            }

            builder.build()
        }
    }
}

object ProgramSerializer : Serializer<Program> {
    override val defaultValue: Program =
        Program.getDefaultInstance().toBuilder().setProgress(0).setPeriod(Macrocycle.Start.ordinal)
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
        get() = Progression.getDefaultInstance().toBuilder().setInitialized(false).setBentArmDynamic(1)
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

fun Progression.getLevelByExerciseCategory(progressiveExercise: ProgressiveExercise): Int {
    return when (progressiveExercise) {
        ProgressiveExercise.UnsupportedStatic -> this.unsupportedStatic
        ProgressiveExercise.SupportedStatic -> this.supportedStatic
        ProgressiveExercise.StraightArmDynamic -> this.straightArmDynamic
        ProgressiveExercise.BentArmDynamic -> this.bentArmDynamic
        ProgressiveExercise.VerticalPull -> this.verticalPull
        ProgressiveExercise.HorizontalPull -> this.horizontalPull
        ProgressiveExercise.VerticalPush -> this.verticalPush
        ProgressiveExercise.HorizontalPush -> this.horizontalPush
        ProgressiveExercise.WeightedHorizontalPush -> this.weightedHorizontalPush
    }
}

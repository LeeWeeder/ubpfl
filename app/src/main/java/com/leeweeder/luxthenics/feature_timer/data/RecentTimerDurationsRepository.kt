package com.leeweeder.luxthenics.feature_timer.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.leeweeder.luxthenics.RecentTimerDurations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.InputStream
import java.io.OutputStream

interface RecentTimerDurationsRepository {
    val recentTimerDurations: Flow<RecentTimerDurations>

    suspend fun addTimerDuration(duration: Int)
}

private val Context.recentTimerDurationsDataStore: DataStore<RecentTimerDurations> by dataStore(
    fileName = "recent_timer_durations.pb", serializer = RecentTimerDurationSerializer
)

class DefaultRecentTimerDurationsRepository(context: Context) : RecentTimerDurationsRepository {
    private val recentTimerDurationsDataStore = context.recentTimerDurationsDataStore

    override val recentTimerDurations = recentTimerDurationsDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(RecentTimerDurations.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun addTimerDuration(duration: Int) {
        recentTimerDurationsDataStore.updateData { preferences ->
            val durationsMap = preferences.durationsMap.toMutableMap()
            if (durationsMap.size == 10) {
                val oldestEntry = durationsMap.minByOrNull { it.value }
                oldestEntry?.let { durationsMap[duration] = it.value }
            } else durationsMap[duration] = System.currentTimeMillis()
            preferences.toBuilder().putAllDurations(durationsMap).build()
        }
    }
}

private object RecentTimerDurationSerializer : Serializer<RecentTimerDurations> {
    override val defaultValue: RecentTimerDurations
        get() = RecentTimerDurations.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): RecentTimerDurations {
        try {
            return RecentTimerDurations.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: RecentTimerDurations, output: OutputStream) {
        t.writeTo(output)
    }

}

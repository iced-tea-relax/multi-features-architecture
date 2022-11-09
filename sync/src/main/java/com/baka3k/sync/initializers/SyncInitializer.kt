package com.baka3k.sync.initializers

import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import com.baka3k.sync.workers.SyncWorker

object Sync {
    fun initialize(context: Context) {
        AppInitializer.getInstance(context).initializeComponent(SyncInitializer::class.java)
    }
}

private const val SyncWorkName = "SyncWorkName"

class SyncInitializer : Initializer<Sync> {
    override fun create(context: Context): Sync {
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                SyncWorkName,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork()
            )
        }
        return Sync
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(WorkManagerInitializer::class.java)
}
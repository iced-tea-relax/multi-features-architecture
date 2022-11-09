package com.baka3k.sync.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.baka3k.core.common.network.Dispatcher
import com.baka3k.core.common.network.HiDispatchers
import com.baka3k.core.data.Synchronizer
import com.baka3k.core.data.movie.repository.MovieRepository
import com.baka3k.core.data.movie.repository.MovieTypeRepository
import com.baka3k.core.datastore.ChangeListVersions
import com.baka3k.core.datastore.HiPreferencesDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

private const val SyncNotificationId = 0
private const val SyncNotificationChannelID = "SyncNotificationChannel"

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.syncWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channel = NotificationChannel(
//            SyncNotificationChannelID,
//            getString(R.string.sync_notification_channel_name),
//            NotificationManager.IMPORTANCE_DEFAULT
//        ).apply {
//            description = getString(R.string.sync_notification_channel_description)
//        }
//        // Register the channel with the system
//        val notificationManager: NotificationManager? =
//            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
//
//        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        SyncNotificationChannelID
    )
//        .setSmallIcon(R.drawable.ic_nia_notification)
//        .setContentTitle(getString(R.string.sync_notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}

// All sync work needs an internet connectionS
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.syncForegroundInfo() = ForegroundInfo(
    SyncNotificationId,
    syncWorkNotification()
)

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val hiPreferences: HiPreferencesDataSource,
    private val movieRepository: MovieRepository,
    private val movieTypeRepository: MovieTypeRepository,
    @Dispatcher(HiDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams), Synchronizer {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val syncedSuccessfully = awaitAll(
            async { movieTypeRepository.sync() },
        ).all { it }

        if (syncedSuccessfully) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()


    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }

    override suspend fun getChangeListVersions(): ChangeListVersions {
        return hiPreferences.getChangeListVersions()
    }

    override suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions) {
        hiPreferences.updateChangeListVersion(update)
    }
}
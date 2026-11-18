package com.devicexpert.app.data.collector
import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class MemoryStorageCollector @Inject constructor(@ApplicationContext private val context: Context) {
    fun getMemoryStorageMetrics(): MemoryStorageResult {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val usedRam = totalRam - memoryInfo.availMem
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorage = statFs.totalBytes
        val availableStorage = statFs.availableBytes
        val usedStorage = totalStorage - availableStorage
        return MemoryStorageResult(usedRam, totalRam, usedStorage, totalStorage)
    }
    data class MemoryStorageResult(val usedRamBytes: Long, val totalRamBytes: Long, val usedStorageBytes: Long, val totalStorageBytes: Long)
}
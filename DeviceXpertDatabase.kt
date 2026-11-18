package com.devicexpert.app.data.local.db
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devicexpert.app.data.local.dao.*
import com.devicexpert.app.data.local.entity.*
@Database(entities = [DeviceEntity::class, TelemetryEntity::class, AlertRuleEntity::class, AlertEventEntity::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class DeviceXpertDatabase : RoomDatabase() {
    abstract val deviceDao: DeviceDao
    abstract val telemetryDao: TelemetryDao
    abstract val alertDao: AlertDao
}
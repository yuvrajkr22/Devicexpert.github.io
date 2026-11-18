package com.devicexpert.app.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devicexpert.app.domain.model.device.*
@Entity(tableName = "devices")
data class DeviceEntity(@PrimaryKey val id: String, val name: String, val category: DeviceCategory, @ColumnInfo(name = "connection_status") val connectionStatus: ConnectionStatus, @ColumnInfo(name = "is_local_host") val isLocalHost: Boolean, @ColumnInfo(name = "last_seen_millis") val lastSeenMillis: Long, @ColumnInfo(name = "metadata_json") val metadataJson: String)
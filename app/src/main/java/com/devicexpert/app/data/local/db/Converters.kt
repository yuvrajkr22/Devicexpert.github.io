package com.devicexpert.app.data.local.db
import androidx.room.TypeConverter
import com.devicexpert.app.domain.model.device.*
class Converters {
    @TypeConverter fun fromDeviceCategory(category: DeviceCategory): String = category.name
    @TypeConverter fun toDeviceCategory(value: String): DeviceCategory = DeviceCategory.valueOf(value)
    @TypeConverter fun fromConnectionStatus(status: ConnectionStatus): String = status.name
    @TypeConverter fun toConnectionStatus(value: String): ConnectionStatus = ConnectionStatus.valueOf(value)
}
package com.devicexpert.app.di
import com.devicexpert.app.data.connector.*
import com.devicexpert.app.domain.connector.*
import com.devicexpert.app.domain.model.device.DeviceCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) object ConnectorModule {
    @Provides @Singleton fun provideConnectorRegistry(android: LocalAndroidDeviceConnector, iot: MqttIotDeviceConnector): ConnectorRegistry = object : ConnectorRegistry {
        override fun getConnector(category: DeviceCategory): DeviceConnector = when (category) { DeviceCategory.ANDROID -> android; DeviceCategory.IOT -> iot; DeviceCategory.COMPUTER -> throw NotImplementedError() }
    }
}
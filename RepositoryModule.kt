package com.devicexpert.app.di
import com.devicexpert.app.data.repository.DeviceRepositoryImpl
import com.devicexpert.app.domain.repository.DeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository
}
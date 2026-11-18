package com.devicexpert.app.di
import android.content.Context
import androidx.room.Room
import com.devicexpert.app.data.local.dao.*
import com.devicexpert.app.data.local.db.DeviceXpertDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) object DatabaseModule {
    @Provides @Singleton fun provideDeviceXpertDatabase(@ApplicationContext context: Context): DeviceXpertDatabase = Room.databaseBuilder(context, DeviceXpertDatabase::class.java, "devicexpert_local_db").fallbackToDestructiveMigration().build()
    @Provides @Singleton fun provideDeviceDao(db: DeviceXpertDatabase): DeviceDao = db.deviceDao
    @Provides @Singleton fun provideTelemetryDao(db: DeviceXpertDatabase): TelemetryDao = db.telemetryDao
    @Provides @Singleton fun provideAlertDao(db: DeviceXpertDatabase): AlertDao = db.alertDao
}
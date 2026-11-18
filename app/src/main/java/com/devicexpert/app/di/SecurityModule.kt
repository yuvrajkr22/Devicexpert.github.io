package com.devicexpert.app.di
import android.content.Context
import com.devicexpert.app.core.security.SecureStorageManager
import com.devicexpert.app.core.security.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideSecureStorageManager(@ApplicationContext context: Context): SecureStorageManager {
        return SecureStorageManager(dataStore = context.dataStore, context = context)
    }
}
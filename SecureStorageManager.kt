package com.devicexpert.app.core.security
import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "devicexpert_secure_prefs")

class SecureStorageManager(private val dataStore: DataStore<Preferences>, context: Context) {
    private val aead: Aead
    init {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "tink_keyset", "secure_keyset_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://devicexpert_master_key")
            .build().keysetHandle
        aead = keysetHandle.getPrimitive(Aead::class.java)
    }
    suspend fun saveSecureString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        val encrypted = aead.encrypt(value.toByteArray(Charsets.UTF_8), key.toByteArray(Charsets.UTF_8))
        val encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        dataStore.edit { it[prefKey] = encoded }
    }
    fun getSecureString(key: String): Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { prefs ->
            prefs[prefKey]?.let { encoded ->
                try {
                    val decoded = Base64.decode(encoded, Base64.NO_WRAP)
                    val decrypted = aead.decrypt(decoded, key.toByteArray(Charsets.UTF_8))
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) { null }
            }
        }
    }
    suspend fun clearSecureString(key: String) {
        dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }
}
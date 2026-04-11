package rs.raf.banka1.mobile.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ClientData(
    val id: Long,
    val name: String,
    val lastName: String,
    val email: String
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> delete(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    suspend fun saveAuthToken(token: String) {
        write(UserPreferencesKeys.AUTH_TOKEN_KEY, token)
    }

    fun readAuthToken(): Flow<String> {
        return read(UserPreferencesKeys.AUTH_TOKEN_KEY, "")
    }

    suspend fun saveClientData(client: ClientData) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.CLIENT_ID_KEY] = client.id
            prefs[UserPreferencesKeys.CLIENT_NAME_KEY] = client.name
            prefs[UserPreferencesKeys.CLIENT_LAST_NAME_KEY] = client.lastName
            prefs[UserPreferencesKeys.CLIENT_EMAIL_KEY] = client.email
        }
    }

    fun readClientData(): Flow<ClientData?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                val id = prefs[UserPreferencesKeys.CLIENT_ID_KEY] ?: return@map null
                ClientData(
                    id = id,
                    name = prefs[UserPreferencesKeys.CLIENT_NAME_KEY] ?: "",
                    lastName = prefs[UserPreferencesKeys.CLIENT_LAST_NAME_KEY] ?: "",
                    email = prefs[UserPreferencesKeys.CLIENT_EMAIL_KEY] ?: ""
                )
            }
    }

    suspend fun saveFcmToken(token: String) {
        write(UserPreferencesKeys.FCM_TOKEN_KEY, token)
    }

    fun readFcmToken(): Flow<String> {
        return read(UserPreferencesKeys.FCM_TOKEN_KEY, "")
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    object UserPreferencesKeys {
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token_key")
        val CLIENT_ID_KEY = longPreferencesKey("client_id_key")
        val CLIENT_NAME_KEY = stringPreferencesKey("client_name_key")
        val CLIENT_LAST_NAME_KEY = stringPreferencesKey("client_last_name_key")
        val CLIENT_EMAIL_KEY = stringPreferencesKey("client_email_key")
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token_key")
    }
}
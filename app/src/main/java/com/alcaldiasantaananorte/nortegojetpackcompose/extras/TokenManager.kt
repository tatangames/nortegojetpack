package com.alcaldiasantaananorte.nortegojetpackcompose.extras

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Extension function to create a DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    companion object {
        val USER_ID_KEY = stringPreferencesKey("USER_ID")
        val USER_TOKEN_KEY = stringPreferencesKey("USER_TOKEN")
    }

    // Guardar el ID del usuario
    suspend fun guardarClienteID(id: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = id
        }
    }

    // Guardar el token de seguridad
    suspend fun guardarClienteTOKEN(token: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_TOKEN_KEY] = token
        }
    }

    // Borrar datos almacenados
    suspend fun deletePreferences() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_TOKEN_KEY)
        }
    }

    // Obtener los datos almacenados como Flow
    val userId: Flow<String> = context.dataStore.data
        .map { prefs ->
            prefs[USER_ID_KEY] ?: ""
        }

    val userToken: Flow<String> = context.dataStore.data
        .map { prefs ->
            prefs[USER_TOKEN_KEY] ?: ""
        }
}
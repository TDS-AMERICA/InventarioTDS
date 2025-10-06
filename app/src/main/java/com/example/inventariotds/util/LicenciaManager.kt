package com.example.inventariotds.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

object LicenciaManager {
    private val KEY_CODIGO = stringPreferencesKey("codigo_licencia")
    private val KEY_FECHA = stringPreferencesKey("fecha_expiracion")

    suspend fun guardarLicencia(context: Context, codigo: String, fecha: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CODIGO] = codigo
            prefs[KEY_FECHA] = fecha
        }
    }

    suspend fun obtenerLicencia(context: Context): Pair<String?, String?> {
        val prefs = context.dataStore.data.first()
        return Pair(prefs[KEY_CODIGO], prefs[KEY_FECHA])
    }

    suspend fun licenciaEsValida(context: Context): Boolean {
        val (_, fechaStr) = obtenerLicencia(context)
        return try {
            if (fechaStr.isNullOrBlank()) return false
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaExpiracion = formato.parse(fechaStr)
            val ahora = Date()
            fechaExpiracion != null && ahora.before(fechaExpiracion)
        } catch (e: Exception) {
            false
        }
    }
}

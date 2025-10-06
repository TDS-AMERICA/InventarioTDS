package com.example.inventariotds.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.inventariotds.util.LicenciaManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainWrapper(context: Context) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    // ✅ Verifica al inicio si hay licencia válida
    LaunchedEffect(Unit) {
        val licencia = LicenciaManager.obtenerLicencia(context)
        val expiracion = licencia?.second

        if (!expiracion.isNullOrBlank()) {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaExp = formato.parse(expiracion)
            val ahora = Date()

            startDestination = if (fechaExp != null && ahora.before(fechaExp)) {
                "funcionario"
            } else {
                "licencia"
            }
        } else {
            startDestination = "licencia"
        }
    }

    // ⏳ Muestra la navegación solo si ya se cargó el destino inicial
    startDestination?.let { start ->
        AppNavigation(navController, start)

        // ⏲ Verificación periódica cada 1 minuto (opcional)
        LaunchedEffect(Unit) {
            while (true) {
                delay(60_000) // 60 segundos
                val licencia = LicenciaManager.obtenerLicencia(context)
                val expiracion = licencia?.second

                if (!expiracion.isNullOrBlank()) {
                    val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val fechaExp = formato.parse(expiracion)
                    val ahora = Date()

                    if (fechaExp != null && ahora.after(fechaExp)) {
                        Toast.makeText(context, "Licencia vencida", Toast.LENGTH_SHORT).show()
                        navController.navigate("licencia") {
                            popUpTo(0) { inclusive = true }
                        }
                        break
                    }
                }
            }
        }
    }
}

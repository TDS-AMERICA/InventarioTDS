package com.example.inventariotds.screens


import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.inventariotds.R
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.util.LicenciaManager
import com.example.inventariotds.util.dataStore
import com.example.inventariotds.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.inventariotds.screens.CustomButton

/*
@Composable
fun ConfiguracionScreen(navController: NavHostController) {
    val opciones = listOf(
        "Ubicación", "Código de barras", "Descripción", "Cantidad", "Lote",
        "Serie", "Fecha de vencimiento", "Fecha de fabricación", "Bulto",
        "Observación", "Part Number"
    )

    val currentRoute = "configuracion"
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("configuracion", Context.MODE_PRIVATE)
    val estadoInputs = remember { mutableStateMapOf<String, Boolean>() }

    // Cargar preferencias
    LaunchedEffect(Unit) {
        opciones.forEach { key ->
            estadoInputs[key] = sharedPreferences.getBoolean(key, true)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) },
        containerColor = AzulOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                color = Blanco,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(opciones) { opcion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = estadoInputs[opcion] ?: true,
                            onCheckedChange = { isChecked ->
                                estadoInputs[opcion] = isChecked
                            }
                        )
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Blanco,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    guardarPreferencias(sharedPreferences, estadoInputs)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Blanco)
            ) {
                Text("Guardar", color = AzulOscuro)
            }
        }
    }
}

fun guardarPreferencias(sharedPreferences: SharedPreferences, estadoInputs: Map<String, Boolean>) {
    with(sharedPreferences.edit()) {
        estadoInputs.forEach { (key, value) ->
            putBoolean(key, value)
        }
        apply()
    }
}


 */



@Composable
fun ConfiguracionScreen(navController: NavHostController) {
    val opciones = listOf(
        "Código interno", "Descripción", "Lote",
        "Serie", "Fecha de ingreso", "Fecha de vencimiento", "Fecha de fabricación", "Bulto",
        "Observación", "Part Number"
    )

    val currentRoute = "configuracion"
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("configuracion", Context.MODE_PRIVATE)
    val estadoInputs = remember { mutableStateMapOf<String, Boolean>() }

    // Snackbar setup
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var mostrarDialogo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val viewModel = viewModel<InventarioViewModel>(viewModelStoreOwner = context as ViewModelStoreOwner)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { importarCsv(it, context, viewModel) }
    }
    var mostrarResetLicencia by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.cargarMaestro(context)
        viewModel.cargarInventariados(context)
    }
    // Cargar preferencias
    LaunchedEffect(Unit) {
        opciones.forEach { key ->
            estadoInputs[key] = sharedPreferences.getBoolean(key, true)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) },
        containerColor = AzulOscuro,
        snackbarHost = { SnackbarHost(snackbarHostState) } // Añadido snackbar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.sello_agua_tds),
                contentDescription = "Logo tds",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(20.dp)
            )

            // Título
            Image(
                painter = painterResource(id = R.drawable.configuracion),
                contentDescription = "configuracion",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .height(80.dp)
            )

            /*Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                color = Amarillo,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(opciones) { opcion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = estadoInputs[opcion] ?: true,
                            onCheckedChange = { isChecked ->
                                estadoInputs[opcion] = isChecked
                            }
                        )
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Blanco,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                    Divider(color = Blanco.copy(alpha = 0.2f))
                    Spacer(Modifier.height(16.dp))

                    CustomButton(text = "COMENZAR DE NUEVO") {
                        mostrarDialogo = true
                    }

                    Spacer(Modifier.height(12.dp))

                    CustomButton(text = "RESET LICENCIA") {
                        mostrarResetLicencia = true   // 👈 abre el diálogo
                    }


                    Spacer(Modifier.height(24.dp)) // margen final
                }
            }
            // Botones
            /* Column(
                 modifier = Modifier.fillMaxWidth(),
                 verticalArrangement = Arrangement.spacedBy(12.dp)
             ) {*/

            Button(
                onClick = {
                    guardarPreferencias(sharedPreferences, estadoInputs)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Configuración guardada correctamente")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Blanco)
            ) {
                Text("Guardar", color = AzulOscuro)
            }

        }

        // Diálogo de confirmación
        ConfirmacionResetDialog(
            visible = mostrarDialogo,
            onDismiss = { mostrarDialogo = false },
            onConfirm = {
                val prefsMaestro = context.getSharedPreferences("maestro", Context.MODE_PRIVATE)
                val prefsInventario = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
                prefsMaestro.edit().clear().apply()
                prefsInventario.edit().clear().apply()
                viewModel.productosMaestro.clear()
                viewModel.productosInventariados.clear()
                mostrarDialogo = false
                Toast.makeText(context, "Datos borrados correctamente", Toast.LENGTH_SHORT).show()
            }
        )
        if (mostrarResetLicencia) {
            AlertDialog(
                onDismissRequest = { mostrarResetLicencia = false },
                title = { Text("Revocar licencia") },
                text = {
                    Text(
                        "Al confirmar, la licencia actual será revocada de forma inmediata. " +
                                "Para continuar usando la aplicación, deberá contactar a TDS para obtener una nueva licencia.\n\n" +
                                "Sitio: https://www.tds.cl\n" +
                                "Teléfono: +56 2 2236 0727\n" +
                                "Email: contacto@tds.cl"
                    , color = AzulOscuro)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarResetLicencia = false
                            scope.launch {
                                resetLicencia(context, navController)  // 👈 limpia y navega a "licencia"
                            }
                        }
                    ) {
                        Text("Confirmar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarResetLicencia = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }


    }

}

@Composable
fun ConfirmacionResetDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = { Text("Confirmar reinicio") },
            text = { Text("¿Estás seguro que quieres borrar todo y comenzar desde cero?", color = AzulOscuro) },
            containerColor = Color.White
        )
    }

}

suspend fun resetLicencia(context: Context, navController: NavController) {
    context.dataStore.edit { it.clear() }
    navController.navigate("licencia") {
        popUpTo("home") { inclusive = true }
    }
}
fun guardarPreferencias(sharedPreferences: SharedPreferences, estadoInputs: Map<String, Boolean>) {
    with(sharedPreferences.edit()) {
        estadoInputs.forEach { (key, value) ->
            putBoolean(key, value)
        }
        apply()
    }
}
@Composable
fun LicenciaCountdown(context: Context) {
    var tiempoRestante by remember { mutableStateOf("Calculando licencia...") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            // Usa LicenciaManager para obtener la fecha
            val (_, fechaStr) = LicenciaManager.obtenerLicencia(context)
            if (!fechaStr.isNullOrBlank()) {
                try {
                    // Tu formato debe coincidir con el que guardas (yyyy-MM-dd HH:mm:ss)
                    val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val fechaExpiracion = formato.parse(fechaStr)
                    val ahora = Date()
                    if (fechaExpiracion != null) {
                        val diffMs = fechaExpiracion.time - ahora.time
                        if (diffMs > 0) {
                            val dias = diffMs / (1000 * 60 * 60 * 24)
                            val horas = (diffMs / (1000 * 60 * 60)) % 24
                            val minutos = (diffMs / (1000 * 60)) % 60
                            tiempoRestante = "Licencia activa: quedan $dias días, $horas horas, $minutos minutos"
                        } else {
                            tiempoRestante = "La licencia ha vencido"
                        }
                    } else {
                        tiempoRestante = "Fecha de licencia inválida"
                    }
                } catch (e: Exception) {
                    tiempoRestante = "Error al leer la licencia"
                }
            } else {
                tiempoRestante = "No hay licencia activa"
            }
        }
    }

    Text(
        text = tiempoRestante,
        color = Amarillo,
        fontSize = 16.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 18.dp),
        textAlign = TextAlign.Center
    )
}

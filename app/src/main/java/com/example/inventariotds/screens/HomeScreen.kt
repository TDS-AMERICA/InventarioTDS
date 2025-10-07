package com.example.inventariotds.screens

/*
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventariotds.R
import com.example.inventariotds.screens.importarCsv
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.viewmodel.InventarioViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel<InventarioViewModel>(viewModelStoreOwner = context as ViewModelStoreOwner)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { importarCsv(it, context, viewModel) }
    }

    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarMaestro(context)
        viewModel.cargarInventariados(context)
    }

    val currentRoute = "home"

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
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_tds),
                contentDescription = "Logo TDS",
                modifier = Modifier
                    .height(60.dp)
                    .padding(top = 20.dp)
            )

            // Título
            Text(
                text = "INICIO",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Amarillo,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomButton(text = "IMPORTAR CSV") {
                    launcher.launch("**")
                }
                CustomButton(text = "LISTADO DE PRODUCTOS") {
                    navController.navigate("listaProductos")
                }
                CustomButton(text = "VER MAESTRO") {
                    navController.navigate("maestro")
                }
                CustomButton(text = "COMENZAR DE NUEVO") {
                    mostrarDialogo = true
                }
                CustomButton(text = "RECONTEO") {
                    navController.navigate("reconteo")
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

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GrisClaro),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, color = AzulOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            text = { Text("¿Estás seguro que quieres borrar todo y comenzar desde cero?") },
            containerColor = Color.White
        )
    }
}
*/



import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventariotds.R
import com.example.inventariotds.screens.importarCsv
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.util.generarCsvInventarioFinal
import com.example.inventariotds.util.shareCsv
import com.example.inventariotds.viewmodel.InventarioViewModel
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch
import com.example.inventariotds.util.dataStore
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.inventariotds.util.LicenciaManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
@Composable

fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel<InventarioViewModel>(viewModelStoreOwner = context as ViewModelStoreOwner)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { importarCsv(it, context, viewModel) }
    }

    val prefsUsuario = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    val responsable = prefsUsuario.getString("responsable", "user") ?: "user"
    val scope = rememberCoroutineScope()

    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarMaestro(context)
        viewModel.cargarInventariados(context)
    }

    // Status dinámico
    var status by remember { mutableStateOf("") }
    var tienda by remember { mutableStateOf("") }
    var centro by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefsInventario = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
        val productosSet = prefsInventario.getStringSet("inventario", emptySet()) ?: emptySet()

        val documentoRaw = productosSet.firstOrNull { it.contains("documento:") }
            ?.split(";")
            ?.firstOrNull { it.startsWith("documento:") }
            ?.removePrefix("documento:")
            ?.lowercase() ?: ""

        if (documentoRaw.isNotBlank()) {
            val base = Regex("^(.*?)(tienda_|centrodecosto_|$)").find(documentoRaw)?.groups?.get(1)?.value ?: ""
            status = base.trim()

            tienda = Regex("tienda_([a-zA-Z0-9]+)").find(documentoRaw)?.groups?.get(1)?.value ?: ""
            centro = Regex("centrodecosto_([a-zA-Z0-9]+)").find(documentoRaw)?.groups?.get(1)?.value ?: ""
        }
    }

    val currentRoute = "home"

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) },
        containerColor = AzulOscuro
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState), // ✅ Habilita el scroll
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Mostrar status (si existe)
            if (status.isNotBlank() || tienda.isNotBlank() || centro.isNotBlank()) {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    if (status.isNotBlank()) Text("Status: $status", color = Blanco, fontSize = 14.sp)
                    if (tienda.isNotBlank()) Text("Tienda: $tienda", color = Blanco, fontSize = 14.sp)
                    if (centro.isNotBlank()) Text("Centrodecosto: $centro", color = Blanco, fontSize = 14.sp)
                }
            }
/*
            Text(
                text = "Responsable: $responsable",
                color = Blanco,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.End
            )


 */
            Image(
                painter = painterResource(id = R.drawable.sello_agua_tds),
                contentDescription = "Logo tds",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Responsable: ${responsable.uppercase()}",
                    color = Blanco,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

                IconButton(
                    onClick = {
                        navController.navigate("funcionario") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_salir),
                        contentDescription = "Salir",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }



            Image(
                painter = painterResource(id = R.drawable.logo_invent),
                contentDescription = "Logo invent",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(40.dp)
            )

            // Título
            Image(
                painter = painterResource(id = R.drawable.inicio),
                contentDescription = "inicio",
                modifier = Modifier.padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .height(80.dp)
            )

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val interaction = remember { MutableInteractionSource() }

                Image(
                    painter = painterResource(R.drawable.importarb),
                    contentDescription = "Importar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(120.dp)                // fija altura (o usa size/ancho específico)
                        .clickable { launcher.launch("*/*") } // el área clickeable = imagen
                        .padding(horizontal = 16.dp)  // padding visual que NO agranda el hitbox
                )
                /*CustomButton(text = "LISTADO DE PRODUCTOS") {
                    navController.navigate("listaProductos")
                }*/
                Image(
                    painter = painterResource(id = R.drawable.listadob),
                    contentDescription = "listado de producto",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(120.dp)
                        .clickable{ navController.navigate("listaProductos") }
                        .padding(horizontal = 16.dp)
                )
                /*
                CustomButton(text = "VER MAESTRO") {
                    navController.navigate("maestro")
                }

                 */
                /*CustomButton(text = "COMENZAR DE NUEVO") {
                    mostrarDialogo = true
                }

                CustomButton(text = "RESET LICENCIA") {
                    scope.launch {
                        resetLicencia(context, navController)
                    }
                }*/

                /*CustomButton(text = "RECONTEO") {
                    navController.navigate("reconteo")
                }*/
               /* CustomButton(text = "EXPORTAR Y ENVIAR CSV") {
                    val file = generarCsvInventarioFinal(context, viewModel.productosInventariados)
                    if (file != null) {
                        shareCsv(context, file)
                    }}

            }*/
            Image(
                painter = painterResource(id = R.drawable.exportarb),
                contentDescription = "exportar",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(120.dp)
                    .clickable{
                        val file = generarCsvInventarioFinal(context, viewModel.productosInventariados)
                        if (file != null) {
                            shareCsv(context, file)
                        }
                    }
                    .padding(horizontal = 16.dp)
            )


            /*
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = {
                    navController.navigate("funcionario")
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.salida),
                        contentDescription = "Salir",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

             */


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
            LicenciaCountdown(context)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}}

    @Composable
    fun CustomButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GrisClaro),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = text, color = AzulOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }


/*@Composable
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
*/

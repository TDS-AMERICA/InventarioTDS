package com.example.inventariotds.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.example.inventariotds.R
import com.example.inventariotds.ui.theme.Amarillo
import com.example.inventariotds.ui.theme.AzulOscuro
import com.example.inventariotds.ui.theme.Blanco
import com.example.inventariotds.ui.theme.BottomNavigationBar
import com.example.inventariotds.viewmodel.InventarioViewModel
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font as GFont

// Proveedor de Google Play Services
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Familia Open Sans descargable
val openSansFamily = FontFamily(
    GFont(GoogleFont("Open Sans"), provider)
)
@Composable
fun ListaProductosScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<InventarioViewModel>(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner
    )
    var query by remember { mutableStateOf(TextFieldValue("")) }


    // Cargar productos inventariados cada vez que abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarInventariados(context)
    }

    val filtrados = viewModel.productosInventariados.filter {
        val codigo = it.codigo ?: ""
        val descripcion = it.descripcion ?: ""
        val codInterno = it.internalCode ?: ""
        codigo.contains(query.text, ignoreCase = true) ||
                descripcion.contains(query.text, ignoreCase = true) ||
                codInterno.contains(query.text, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            val currentRoute = ""
            BottomNavigationBar(navController, currentRoute)
        },
        containerColor = AzulOscuro
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuro)
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

        Text("LISTADO DE PRODUCTOS", style = MaterialTheme.typography.headlineMedium, color = Amarillo,     fontFamily = openSansFamily )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar código o descripción", color = Blanco) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Blanco),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
// ⬇⬇⬇ CONTADOR DE REGISTROS AQUÍ ⬇⬇⬇
        Text(
            text = if (query.text.isBlank())
                "Registros guardados: ${viewModel.productosInventariados.size}"
            else
                "Registros encontrados: ${filtrados.size} / ${viewModel.productosInventariados.size}",
            color = Blanco,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.Start),
            style = MaterialTheme.typography.bodyLarge
        )
        if (filtrados.isEmpty()) {
            Text("No hay productos", color = Blanco)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filtrados) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Puedes mostrar solo los campos que quieras aquí:
                            if (!producto.codigo.isNullOrBlank()) Text("Código de barras: ${producto.codigo}", color = AzulOscuro)
                            if (!producto.descripcion.isNullOrBlank()) Text("Descripción: ${producto.descripcion}", color = AzulOscuro)
                            if (!producto.internalCode.isNullOrBlank()) Text("Código interno: ${producto.internalCode}", color = AzulOscuro)
                            if (!producto.ubicacion.isNullOrBlank()) Text("Ubicación: ${producto.ubicacion}", color = AzulOscuro)
                            if (producto.cantidadContada > 0) Text("Cantidad: ${producto.cantidadContada}", color = AzulOscuro)
                            if (!producto.lote.isNullOrBlank()) Text("Lote: ${producto.lote}", color = AzulOscuro)
                            if (!producto.serie.isNullOrBlank()) Text("Serie: ${producto.serie}", color = AzulOscuro)
                            if (!producto.fechaVencimiento.isNullOrBlank()) Text("Fecha de vencimiento: ${producto.fechaVencimiento}", color = AzulOscuro)
                            if (!producto.fechaFabricacion.isNullOrBlank()) Text("Fecha de fabricación: ${producto.fechaFabricacion}", color = AzulOscuro)
                            if (!producto.bulto.isNullOrBlank()) Text("Bulto: ${producto.bulto}", color = AzulOscuro)
                            if (!producto.observacion.isNullOrBlank()) Text("Observación: ${producto.observacion}", color = AzulOscuro)
                            if (!producto.partNumber.isNullOrBlank()) Text("Part Number: ${producto.partNumber}", color = AzulOscuro)
                            if (!producto.responsable.isNullOrBlank()) Text("Responsable: ${producto.responsable}", color = AzulOscuro)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blanco)
        ) {
            Text("Volver", color = AzulOscuro)
        }
    }
}
}
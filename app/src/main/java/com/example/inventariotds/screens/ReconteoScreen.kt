package com.example.inventariotds.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun ReconteoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)

    val productos = remember { mutableStateListOf<MutableMap<String, String>>() }
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var productoSeleccionado by remember { mutableStateOf<MutableMap<String, String>?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // 🔥 Leer desde JSON usando Gson
    LaunchedEffect(Unit) {
        productos.clear()
        val json = sharedPreferences.getString("inventario_lista", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Producto>>() {}.type
            val lista = Gson().fromJson<List<Producto>>(json, type)
            lista.forEach { producto ->
                val campos = mutableMapOf<String, String>()
                campos["Código interno"] = producto.internalCode ?: ""
                campos["Código de barras"] = producto.codigo
                campos["Descripción"] = producto.descripcion
                campos["Ubicación"] = producto.ubicacion
                campos["Cantidad"] = producto.cantidadContada.toString()
                campos["Lote"] = producto.lote ?: ""
                campos["Serie"] = producto.serie ?: ""
                campos["Fecha de vencimiento"] = producto.fechaVencimiento ?: ""
                campos["Fecha de fabricación"] = producto.fechaFabricacion ?: ""
                campos["Bulto"] = producto.bulto ?: ""
                campos["Observación"] = producto.observacion ?: ""
                campos["Part Number"] = producto.partNumber ?: ""
                campos["Fue contado"] = producto.fueContado.toString()
                campos["Fecha de ingreso"] = producto.fechaIngreso ?: ""
                campos["Responsable"] = producto.responsable ?: ""
                // Opcional: para mantener compatibilidad con conteos
                campos["Conteo"] = "1"
                productos.add(campos)
            }
        }
    }

    val filtrados = productos.filter {
        val codigo = it["Código de barras"] ?: ""
        val descripcion = it["Descripción"] ?: ""
        codigo.contains(query.text, ignoreCase = true) || descripcion.contains(query.text, ignoreCase = true)
    }

    val currentRoute = "reconteo"

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) },
        containerColor = AzulOscuro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Reconteo", style = MaterialTheme.typography.headlineMedium, color = Amarillo)

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar por código o descripción", color = Blanco) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Blanco),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (filtrados.isEmpty()) {
                Text("No hay productos", color = Blanco)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filtrados) { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .clickable {
                                    productoSeleccionado = producto
                                    showDialog = true
                                },
                            colors = CardDefaults.cardColors(containerColor = Blanco)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text("Código de barras: ${producto["Código de barras"]}", color = AzulOscuro)
                                Text("Descripción: ${producto["Descripción"]}", color = AzulOscuro)
                                Text("Ubicación: ${producto["Ubicación"]}", color = AzulOscuro)
                                Text("Cantidad actual: ${producto["Cantidad"]}", color = AzulOscuro)
                            }
                        }
                    }
                }
            }

            if (showDialog && productoSeleccionado != null) {
                var nuevaCantidad by remember { mutableStateOf(productoSeleccionado!!["Cantidad"] ?: "0") }
                val conteoActual = (productoSeleccionado!!["Conteo"]?.toIntOrNull() ?: 0) + 1

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            productoSeleccionado!!["Cantidad"] = nuevaCantidad
                            productoSeleccionado!!["Conteo"] = conteoActual.toString()

                            // 🔥 Guardar de vuelta en JSON:
                            val gson = Gson()
                            val listaProductos = productos.map { campos ->
                                Producto(
                                    internalCode = campos["Código interno"],
                                    codigo = campos["Código de barras"] ?: "",
                                    descripcion = campos["Descripción"] ?: "",
                                    ubicacion = campos["Ubicación"] ?: "",
                                    cantidadContada = campos["Cantidad"]?.toIntOrNull() ?: 0,
                                    lote = campos["Lote"],
                                    serie = campos["Serie"],
                                    fechaVencimiento = campos["Fecha de vencimiento"],
                                    fechaFabricacion = campos["Fecha de fabricación"],
                                    bulto = campos["Bulto"],
                                    observacion = campos["Observación"],
                                    partNumber = campos["Part Number"],
                                    fueContado = campos["Fue contado"].toBoolean(),
                                    fechaIngreso = campos["Fecha de ingreso"],
                                    responsable = campos["Responsable"]
                                )
                            }
                            val json = gson.toJson(listaProductos)
                            sharedPreferences.edit().putString("inventario_lista", json).apply()

                            showDialog = false
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Conteo $conteoActual") },
                    text = {
                        OutlinedTextField(
                            value = nuevaCantidad,
                            onValueChange = { nuevaCantidad = it },
                            label = { Text("Nueva cantidad") }
                        )
                    }
                )
            }
        }
    }
}

package com.example.inventariotds.screens
/*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.nativeCanvas
import androidx.navigation.NavHostController

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventariotds.ui.theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EstadisticasScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    val productos = remember { mutableStateListOf<Map<String, String>>() }
    val ubicaciones = remember { mutableStateListOf<String>() }
    val productosPorUbicacion = remember { mutableStateMapOf<String, List<Map<String, String>>>() }
    val ubicacionesExpandibles = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        val productosSet = sharedPreferences.getStringSet("inventario", emptySet()) ?: emptySet()
        productos.clear()
        productosSet.forEach { producto ->
            val campos = producto.split(";").mapNotNull {
                val partes = it.split(":")
                if (partes.size >= 2) partes[0] to partes.drop(1).joinToString(":") else null
            }.toMap()
            if (campos.isNotEmpty()) productos.add(campos)
        }

        val agrupados = productos.groupBy { it["Ubicación"].orEmpty() }
        productosPorUbicacion.clear()
        productosPorUbicacion.putAll(agrupados)
        ubicaciones.clear()
        ubicaciones.addAll(agrupados.keys)
    }

    val filtradas = ubicaciones.filter { it.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, "estadisticas") },
        containerColor = AzulOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por ubicación", color = Blanco) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Blanco),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filtradas) { ubicacion ->
                    val expandido = ubicacionesExpandibles[ubicacion] == true
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { ubicacionesExpandibles[ubicacion] = !expandido }
                            .padding(vertical = 4.dp)
                    ) {
                        Text("▶", fontSize = 18.sp, color = Amarillo, modifier = Modifier.padding(end = 4.dp))
                        Text(ubicacion, color = Blanco, fontSize = 16.sp)
                    }

                    if (expandido) {
                        productosPorUbicacion[ubicacion]?.let { lista ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            ) {
                                lista.forEach { producto ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Blanco)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            producto["Código de barras"]?.let {
                                                Text("Código de barras: $it", color = AzulOscuro)
                                            }
                                            producto["Descripción"]?.let {
                                                Text("Descripción: $it", color = AzulOscuro)
                                            }
                                            producto["Cantidad"]?.let {
                                                Text("Cantidad: $it", color = AzulOscuro)
                                            }
                                            producto["Fecha de ingreso"]?.let {
                                                Text("Fecha de ingreso: $it", color = AzulOscuro)
                                            }
                                            producto["Responsable"]?.let {
                                                Text("Responsable: $it", color = AzulOscuro)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                GraficoPastel(data = lista)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GraficoPastel(data: List<Map<String, String>>) {
    val cantidades = data.groupBy { it["Código de barras"].orEmpty() }
        .mapValues { it.value.sumOf { it["Cantidad"]?.toIntOrNull() ?: 0 } }

    val total = cantidades.values.sum()
    val colores = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
        var inicio = 0f
        cantidades.entries.forEachIndexed { index, (codigo, cantidad) ->
            val sweep = 360f * (cantidad.toFloat() / total.toFloat())
            drawArc(
                color = colores.getOrElse(index) { Color.Gray },
                startAngle = inicio,
                sweepAngle = sweep,
                useCenter = true
            )

            // Etiqueta encima del color
            val angle = inicio + sweep / 2f
            val x = center.x + 100 * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = center.y + 100 * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    codigo,
                    x,
                    y,
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 24f
                        textAlign = Paint.Align.CENTER
                    }
                )
            }

            inicio += sweep
        }
    }
}

}
 */


import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.nativeCanvas
import androidx.navigation.NavHostController
import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventariotds.R
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EstadisticasScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    val productos = remember { mutableStateListOf<Map<String, String>>() }
    val ubicaciones = remember { mutableStateListOf<String>() }
    val productosPorUbicacion = remember { mutableStateMapOf<String, List<Map<String, String>>>() }
    val ubicacionesExpandibles = remember { mutableStateMapOf<String, Boolean>() }

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
                productos.add(campos)
            }
        }

        val agrupados = productos
            .filter { it["Ubicación"].orEmpty().isNotBlank() }
            .groupBy { it["Ubicación"].orEmpty() }

        productosPorUbicacion.clear()
        productosPorUbicacion.putAll(agrupados)
        ubicaciones.clear()
        ubicaciones.addAll(agrupados.keys)
    }


    val filtradas = ubicaciones.filter { it.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, "estadisticas") },
        containerColor = AzulOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
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
                painter = painterResource(id = R.drawable.estadisticas),
                contentDescription = "Estadistica",
                modifier = Modifier.padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .height(80.dp)
            )
            /*Text(
                text = "Estadisticas",
                style = MaterialTheme.typography.headlineMedium,
                color = Amarillo,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por ubicación", color = Blanco) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Blanco),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filtradas) { ubicacion ->
                    val expandido = ubicacionesExpandibles[ubicacion] == true
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { ubicacionesExpandibles[ubicacion] = !expandido }
                            .padding(vertical = 4.dp)
                    ) {
                        Text("▶", fontSize = 18.sp, color = Amarillo, modifier = Modifier.padding(end = 4.dp))
                        Text(
                            text = ubicacion,
                            color = Blanco,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    if (expandido) {
                        productosPorUbicacion[ubicacion]?.let { lista ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            ) {
                                lista.forEach { producto ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Blanco)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            producto["Código de barras"]?.let {
                                                Text("Código de barras: $it", color = AzulOscuro)
                                            }
                                            producto["Descripción"]?.let {
                                                Text("Descripción: $it", color = AzulOscuro)
                                            }
                                            producto["Cantidad"]?.let {
                                                Text("Cantidad: $it", color = AzulOscuro)
                                            }
                                            producto["Fecha de ingreso"]?.let {
                                                Text("Fecha de ingreso: $it", color = AzulOscuro)
                                            }
                                            producto["Responsable"]?.let {
                                                Text("Responsable: $it", color = AzulOscuro)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                GraficoPastel(data = lista)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GraficoPastel(data: List<Map<String, String>>) {
    val cantidades = data.groupBy { it["Código de barras"].orEmpty() }
        .mapValues { it.value.sumOf { it["Cantidad"]?.toIntOrNull() ?: 0 } }

    val total = cantidades.values.sum()
    val colores = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
        var inicio = 0f
        cantidades.entries.forEachIndexed { index, (codigo, cantidad) ->
            val sweep = 360f * (cantidad.toFloat() / total.toFloat())
            drawArc(
                color = colores.getOrElse(index) { Color.Gray },
                startAngle = inicio,
                sweepAngle = sweep,
                useCenter = true
            )

            val angle = inicio + sweep / 2f
            val x = center.x + 100 * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = center.y + 100 * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    codigo,
                    x,
                    y,
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 32f // 🔥 Aquí se ajusta el tamaño
                        textAlign = Paint.Align.CENTER
                    }
                )
            }

            inicio += sweep
        }
    }
}


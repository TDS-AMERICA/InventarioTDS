package com.example.inventariotds.screens

import android.content.Context
import androidx.compose.foundation.Image


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.viewmodel.InventarioViewModel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//imports de ultimo agregado
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.inventariotds.R

@Composable
fun InventarioScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel =
        viewModel<InventarioViewModel>(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val currentRoute = "inventario"
    val sharedPrefs = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    val responsable = sharedPrefs.getString("responsable", "jc") ?: "jc"

    //ultimo agregado
    val focusManager = LocalFocusManager.current
    val firstInputFocusRequester = remember { FocusRequester() }

    val campos = listOf(
        "Ubicación", "Código de barras", "Código interno", "Descripción", "Cantidad", "Lote",
        "Serie", "Fecha de ingreso", "Fecha de vencimiento", "Fecha de fabricación", "Bulto",
        "Observación", "Part Number"
    )

    val estadoInputs = remember { mutableStateMapOf<String, String>() }
    val configuracion = context.getSharedPreferences("configuracion", Context.MODE_PRIVATE)
    val datosMap = estadoInputs.mapValues { it.value }

    //ultima modificacion
    var fijarUbicacion by remember { mutableStateOf(false) }
    var ubicacionFijada by remember { mutableStateOf("") }
    val focusRequesters = remember { campos.associateWith { FocusRequester() } }
    var barrido by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarInventariados(context)
        // Resto de inicialización
        campos.forEach { campo ->
            estadoInputs[campo] = ""
        }
    }

    // Autocompletar Descripción al ingresar Código de barras
    LaunchedEffect(estadoInputs["Código de barras"]) {
        val codigo = estadoInputs["Código de barras"] ?: ""
        if (codigo.isNotBlank()) {
            val producto = viewModel.productosMaestro.find { it.codigo == codigo }
            if (producto != null) {
                estadoInputs["Descripción"] = producto.descripcion
            }
        }
    }

    // Autocompletar Código de barras y Código interno al ingresar Descripción
    LaunchedEffect(estadoInputs["Descripción"]) {
        val descripcion = estadoInputs["Descripción"] ?: ""
        if (descripcion.isNotBlank()) {
            val producto = viewModel.productosMaestro.find {
                it.descripcion.equals(
                    descripcion,
                    ignoreCase = true
                )
            }
            if (producto != null) {
                estadoInputs["Código de barras"] = producto.codigo
            }
        }
    }

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
            Image(
                painter = painterResource(id = R.drawable.sello_agua_tds),
                contentDescription = "Logo tds",
                modifier = Modifier
                    .height(20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.inventario),
                contentDescription = "inventario",
                modifier = Modifier.padding(vertical = 16.dp)
                    .height(80.dp)
            )
            /*Text(
                text = "Inventario",
                style = MaterialTheme.typography.headlineMedium,
                color = Amarillo,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/
            var errorCantidad by remember { mutableStateOf(false) }
            var errorUbicacion by remember { mutableStateOf(false) }

            campos.forEachIndexed { index, campo ->
                val mostrarCampo = configuracion.getBoolean(campo, true)
                if (mostrarCampo) {
                    val esFechaIngreso = campo == "Fecha de ingreso"
                    val value = when {
                        campo == "Ubicación" && fijarUbicacion -> ubicacionFijada
                        esFechaIngreso && estadoInputs[campo].isNullOrBlank() -> {
                            SimpleDateFormat(
                                "dd/MM/yyyy HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date()).also {
                                estadoInputs[campo] = it
                            }
                        }

                        else -> estadoInputs[campo] ?: ""
                    }
                    val imeAction =
                        if (index == campos.lastIndex) ImeAction.Done else ImeAction.Next

                    if (campo == "Ubicación") {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = campo,
                                    color = Blanco,
                                    modifier = Modifier.weight(1f)
                                )
                                Checkbox(
                                    checked = fijarUbicacion,
                                    onCheckedChange = { isChecked ->
                                        fijarUbicacion = isChecked
                                        if (isChecked) {
                                            ubicacionFijada = estadoInputs["Ubicación"] ?: ""
                                        } else {
                                            estadoInputs["Ubicación"] = ubicacionFijada
                                            ubicacionFijada = ""
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Blanco,
                                        uncheckedColor = Blanco,
                                        checkmarkColor = AzulOscuro
                                    )
                                )
                            }

                            OutlinedTextField(
                                value = if (fijarUbicacion) ubicacionFijada else (estadoInputs[campo] ?: ""),
                                onValueChange = {
                                    if (!fijarUbicacion) {
                                        estadoInputs[campo] = it
                                        errorUbicacion = it.isBlank()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .focusRequester(focusRequesters[campo]!!),
                                textStyle = LocalTextStyle.current.copy(color = Blanco),
                                singleLine = true,
                                enabled = !fijarUbicacion,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = errorUbicacion
                            )
                            if (errorUbicacion) {
                                Text(
                                    "El campo Ubicación no puede estar vacío.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else if (campo == "Cantidad") {
                        // Validación SOLO para cantidad
                        OutlinedTextField(
                            value = estadoInputs[campo] ?: "",
                            onValueChange = {
                                estadoInputs[campo] = it
                                errorCantidad = it.isNotBlank() && it.any { c -> !c.isDigit() }
                            },
                            label = { Text(campo, color = Blanco) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .focusRequester(focusRequesters[campo]!!),
                            textStyle = LocalTextStyle.current.copy(color = Blanco),
                            singleLine = true,
                            enabled = when {
                                campo == "Cantidad" && barrido -> false
                                esFechaIngreso -> false
                                else -> true
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = imeAction
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = { focusManager.clearFocus() }
                            ),
                            isError = errorCantidad
                        )
                        if (errorCantidad) {
                            Text(
                                "Solo se permiten números en este campo",
                                color = androidx.compose.ui.graphics.Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        if (campo == "Cantidad" && barrido && estadoInputs[campo].isNullOrBlank()) {
                            estadoInputs[campo] = "1"
                        }

                        OutlinedTextField(
                            value = estadoInputs[campo] ?: "",
                            onValueChange = { estadoInputs[campo] = it },
                            label = { Text(campo, color = Blanco) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .focusRequester(focusRequesters[campo]!!),
                            textStyle = LocalTextStyle.current.copy(color = Blanco),
                            singleLine = true,
                            enabled = when {
                                campo == "Cantidad" && barrido -> false
                                esFechaIngreso -> false
                                else -> true
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = imeAction
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = { focusManager.clearFocus() }
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ✅ Botón "Guardar"
                Button(
                    onClick = {
                        val codigo = estadoInputs["Código de barras"] ?: ""
                        val cantidad = estadoInputs["Cantidad"]?.toIntOrNull() ?: 0
                        val Ubicacion = estadoInputs["Ubicación"] ?: ""

                        if (Ubicacion.isNotBlank() && codigo.isNotBlank() && (cantidad > 0 || barrido)) {
                            val datosMap = estadoInputs.mapValues { it.value }
                            if (barrido) {
                                viewModel.registrarProductoBarrido(estadoInputs.toMap(), responsable, context)
                            } else {
                                viewModel.registrarProductoManual(estadoInputs.toMap(), responsable, context)
                            }
                            viewModel.persistirInventariados(context)
                            viewModel.persistirMaestro(context)

                            campos.forEach { campo ->
                                if (campo == "Ubicación" && fijarUbicacion) {
                                    estadoInputs[campo] = ubicacionFijada
                                } else {
                                    estadoInputs[campo] = ""
                                }
                            }

                            val siguienteCampo = campos.firstOrNull { campo ->
                                !(campo == "Ubicación" && fijarUbicacion) && campo != "Fecha de ingreso"
                            }

                            siguienteCampo?.let { campo ->
                                focusRequesters[campo]?.requestFocus()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Blanco)
                ) {
                    Text("Guardar", color = AzulOscuro)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // ✅ Checkbox "Barrido" a la derecha
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Barrido", color = Blanco)
                    Checkbox(
                        checked = barrido,
                        onCheckedChange = {
                            barrido = it
                            if (barrido) {
                                estadoInputs["Cantidad"] = "1"
                            } else {
                                estadoInputs["Cantidad"] = ""
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Blanco,
                            uncheckedColor = Blanco,
                            checkmarkColor = AzulOscuro
                        )
                    )

                }
            }


        }
    }
}
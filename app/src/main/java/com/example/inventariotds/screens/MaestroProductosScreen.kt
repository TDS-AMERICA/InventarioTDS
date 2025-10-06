package com.example.inventariotds.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inventariotds.R
import com.example.inventariotds.ui.theme.*
import com.example.inventariotds.viewmodel.InventarioViewModel

@Composable
fun MaestroProductosScreen(navController: NavHostController) {
    val viewModel = viewModel<InventarioViewModel>(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val currentRoute = "maestro"
    var query by remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(Unit) {
        println("Total productos en el ViewModel: ${viewModel.productosMaestro.size}")
    }
    // Solo se filtra por código o descripción
    val productosFiltrados = viewModel.productosMaestro.filter {
        it.codigo.contains(query.text, ignoreCase = true) ||
                it.descripcion.contains(query.text, ignoreCase = true)
    }

    // Agrupa y elimina duplicados: clave por código y código interno
    val productosUnicos = productosFiltrados
        .groupBy { it.codigo to it.internalCode }
        .map { (_, lista) ->
            // Si alguno está contado, muestra ese; si no, el primero
            lista.find { it.fueContado } ?: lista.first()
        }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, currentRoute) },
        containerColor = AzulOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
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
                painter = painterResource(id = R.drawable.maestro),
                contentDescription = "Maestro productos",
                modifier = Modifier.padding(vertical = 16.dp)
                    .height(80.dp)
            )

            /*Text(
                text = "Maestro de Productos",
                fontSize = 22.sp,
                color = Amarillo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/


            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar", color = Blanco) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Blanco),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            val total = productosUnicos.size
            val contados = productosUnicos.count { it.fueContado }
            val sinContar = total - contados

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total: $total", color = Blanco, style = MaterialTheme.typography.bodyLarge)
                Text("Contados: $contados", color = Blanco, style = MaterialTheme.typography.bodyLarge)
                Text("Sin contar: $sinContar", color = Blanco, style = MaterialTheme.typography.bodyLarge)
            }

            LazyColumn {
                items(productosUnicos) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Código de barras: ${producto.codigo}", color = AzulOscuro)
                                Text("Código interno: ${producto.internalCode}", color = AzulOscuro)
                                Text("Descripción: ${producto.descripcion}", color = AzulOscuro)
                            }
                            Image(
                                painter = painterResource(
                                    id = if (producto.fueContado) R.drawable.ic_check else R.drawable.ic_cross
                                ),
                                contentDescription = "Estado",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

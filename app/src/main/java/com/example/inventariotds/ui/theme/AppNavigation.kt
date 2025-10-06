package com.example.inventariotds.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inventariotds.screens.BarcodeListScreen
import com.example.inventariotds.screens.ConfiguracionScreen
import com.example.inventariotds.screens.EstadisticasScreen
import com.example.inventariotds.screens.FuncionarioScreen
import com.example.inventariotds.screens.HomeScreen
import com.example.inventariotds.screens.InventarioScreen
import com.example.inventariotds.screens.LicenciaScreen
import com.example.inventariotds.screens.ListaProductosScreen
import com.example.inventariotds.screens.MaestroProductosScreen
import com.example.inventariotds.screens.ReconteoScreen

@Composable
fun AppNavigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable("home") { HomeScreen(navController) }
        composable("inventario") { InventarioScreen(navController) }
        composable("configuracion") { ConfiguracionScreen(navController) }
        composable("listaProductos") { ListaProductosScreen(navController) } // ✅ Agregado
        composable("estadisticas") { EstadisticasScreen(navController) }
        composable("barcodeList") { BarcodeListScreen(navController) } // ✅ Nueva pantalla
        composable("maestro") { MaestroProductosScreen(navController)
        }
        composable("funcionario") { FuncionarioScreen(navController) }
        composable("reconteo") { ReconteoScreen(navController) }
        composable("licencia") { LicenciaScreen(navController) }
    }
}

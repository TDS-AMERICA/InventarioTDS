package com.example.inventariotds.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventariotds.R

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String) {
    /*val items = listOf(
        BottomNavItem("inventario", "Inventario", R.drawable.ic_inventario2),
        BottomNavItem("estadisticas", "Estadísticas", R.drawable.ic_est2),
        BottomNavItem("home", "Inicio", R.drawable.ic_inicio2),
        //BottomNavItem("notificaciones", "Notificaciones", R.drawable.ic_notifications),
        BottomNavItem("maestro", "Listado", R.drawable.ic_maestro2), // ✅ Nuevo ítem
        BottomNavItem("configuracion", "Configuración", R.drawable.ic_config2)
    )*/
    val items = listOf(
        BottomNavItem("inventario", "Inventario",  R.drawable.ic_inventario2,  R.drawable.ic_inventario1),
        BottomNavItem("estadisticas","Estadísticas",R.drawable.ic_est2,        R.drawable.ic_est1),
        BottomNavItem("home",       "Inicio",      R.drawable.ic_inicio2,     R.drawable.ic_inicio1),
        BottomNavItem("maestro",    "Listado",     R.drawable.ic_maestro2,    R.drawable.ic_maestro1),
        BottomNavItem("configuracion","Configuración",R.drawable.ic_config2,  R.drawable.lc_config1)
    )


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF001F4D))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            IconButton(onClick = {
                if (!isSelected) {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        popUpTo("home") { inclusive = false }
                    }
                }
            }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ) {
                    if (isSelected) {
                        Surface(
                            color = Color.White,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.size(40.dp)
                        ) {}
                    }

                    val iconRes = if (isSelected) item.selectedIcon ?: item.icon else item.icon

                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
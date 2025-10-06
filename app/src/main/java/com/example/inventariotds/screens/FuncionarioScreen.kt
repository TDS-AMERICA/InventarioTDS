package com.example.inventariotds.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.inventariotds.R
import com.example.inventariotds.ui.theme.Amarillo
import com.example.inventariotds.ui.theme.AzulOscuro
import com.example.inventariotds.ui.theme.Blanco

@Composable
fun FuncionarioScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    var nombre by remember { mutableStateOf(prefs.getString("responsable", "") ?: "") }

    // 🔵 Valida si el nombre cumple los requisitos (mínimo y máximo)
    val nombreValido = nombre.length in 3..20

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuro)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sello_agua_tds),
            contentDescription = "Logo tds",
            modifier = Modifier
                .height(60.dp)
                .padding(bottom = 30.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.logo_invent),
            contentDescription = "Logo invent",
            modifier = Modifier
                .height(60.dp)
                .padding(bottom = 10.dp)
        )

        Text("Ingrese su nombre", color = Amarillo, fontSize = 20.sp)

        OutlinedTextField(
            value = nombre,
            // ⬇ Solo permite hasta 20 caracteres
            onValueChange = { if (it.length <= 20) nombre = it },
            label = { Text("Funcionario", color = Blanco) },
            singleLine = true,
            isError = nombre.isNotEmpty() && !nombreValido,  // ⬅ se pone rojo si está mal
            supportingText = {
                if (nombre.isNotEmpty() && !nombreValido)
                    Text("El nombre debe tener entre 3 y 20 caracteres.", color = Color.Red, fontSize = 12.sp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = LocalTextStyle.current.copy(color = Blanco)
        )

        Button(
            onClick = {
                prefs.edit().putString("responsable", nombre).apply()
                navController.navigate("home") {
                    popUpTo("funcionario") { inclusive = true }
                }
            },
            enabled = nombreValido, // ⬅ solo habilitado si el nombre es válido
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blanco)

        ) {
            Text("Ingresar", color = AzulOscuro, fontSize = 16.sp)
        }
    }
}

package com.example.inventariotds.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.inventariotds.ui.theme.Amarillo
import com.example.inventariotds.ui.theme.AzulOscuro
import com.example.inventariotds.ui.theme.Blanco
import com.example.inventariotds.util.LicenciaManager
import kotlinx.coroutines.launch
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.example.inventariotds.R
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenciaScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    // 1. Crear el focusRequester
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // 2. Hacer focus automático al entrar
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun procesarLicencia() {
        val partes = input.split("|")
        if (partes.size == 2) {
            val codigo = partes[0].trim()
            val fecha = partes[1].trim()
            if (codigo.isNotBlank() && fecha.isNotBlank()) {
                scope.launch {
                    LicenciaManager.guardarLicencia(context, codigo, fecha)
                    navController.navigate("funcionario") {
                        popUpTo("licencia") { inclusive = true }
                    }
                }
            } else {
                mensajeError = "Código o fecha inválida"
            }
        } else {
            mensajeError = "Debe ingresar en formato CÓDIGO|FECHA"
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AzulOscuro
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

            Text(
                "Ingrese Código de Licencia y Fecha",
                color = Amarillo,
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    mensajeError = ""
                },
                label = { Text("Código|Fecha", color = Blanco) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { procesarLicencia() }
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Blanco),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .focusRequester(focusRequester),

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Blanco,
                    unfocusedBorderColor = Blanco,

                )
                        )

            if (mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { procesarLicencia() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blanco)
            ) {
                Text("Ingresar", color = AzulOscuro, fontSize = 18.sp)
            }
        }
    }
}

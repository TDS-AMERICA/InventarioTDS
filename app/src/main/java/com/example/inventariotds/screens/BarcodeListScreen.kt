package com.example.inventariotds.screens


import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inventariotds.ui.theme.AzulOscuro
import com.example.inventariotds.ui.theme.Blanco
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BarcodeListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("inventario", Context.MODE_PRIVATE)
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    val productosGuardados = remember { mutableStateListOf<Map<String, String>>() }
    var productoSeleccionado by remember { mutableStateOf<Map<String, String>?>(null) }

    fun buscarProductos() {
        val productosSet = sharedPreferences.getStringSet("productos", emptySet()) ?: emptySet()
        productosGuardados.clear()

        productosSet.forEach { producto ->
            val campos = producto.split(";").map { it.split(":") }
            val mapa = campos.associate { it[0] to it[1] }
            if (
                mapa["ubicacion"]?.contains(searchQuery, ignoreCase = true) == true ||
                mapa["codigo"]?.contains(searchQuery, ignoreCase = true) == true ||
                mapa["descripcion"]?.contains(searchQuery, ignoreCase = true) == true
            ) {
                productosGuardados.add(mapa)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuro)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por ubicación, código o descripción", color = Blanco) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    buscarProductos()
                    keyboardController?.hide()
                }
            ),
            textStyle = LocalTextStyle.current.copy(color = Blanco)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productosGuardados) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { productoSeleccionado = producto }, // ✅ Al hacer clic, muestra la alerta
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Ubicación: ${producto["ubicacion"]}", color = AzulOscuro)
                        Text("Código: ${producto["codigo"]}", color = AzulOscuro)
                        Text("Descripción: ${producto["descripcion"]}", color = AzulOscuro)
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

    // ✅ Alerta de confirmación para imprimir
    if (productoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { productoSeleccionado = null },
            title = { Text("Confirmar impresión") },
            text = { Text("¿Estás seguro de que quieres imprimir el código de barras?") },
            confirmButton = {
                Button(onClick = {
                    imprimirCodigo(context, productoSeleccionado?.get("codigo") ?: "", productoSeleccionado!!)
                    productoSeleccionado = null
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { productoSeleccionado = null }) {
                    Text("No")
                }
            }
        )
    }
}



fun generateBarcode(data: String): Bitmap? {
    return try {
        val bitMatrix: BitMatrix =
            MultiFormatWriter().encode(data, BarcodeFormat.CODE_128, 600, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun imprimirCodigo(context: Context, codigo: String, producto: Map<String, String>) {
    val barcodeBitmap = generateBarcode(codigo) ?: return

    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = object : PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: android.os.Bundle?
        ) {
            callback.onLayoutFinished(
                PrintDocumentInfo.Builder("barcode_print")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build(),
                true
            )
        }

        override fun onWrite(
            pages: Array<android.print.PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback
        ) {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(600, 400, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val textPaint = android.graphics.Paint().apply { textSize = 20f }
            val barcodePaint = android.graphics.Paint()

            // Título
            canvas.drawText("Código de Barras", 200f, 50f, textPaint)

            // Código de Barras
            val scaledBarcode = Bitmap.createScaledBitmap(barcodeBitmap, 500, 150, false)
            canvas.drawBitmap(scaledBarcode, 50f, 80f, barcodePaint)

            // Datos del producto
            canvas.drawText("Ubicación: ${producto["ubicacion"]}", 50f, 260f, textPaint)
            canvas.drawText("Código: ${producto["codigo"]}", 50f, 290f, textPaint)
            canvas.drawText("Descripción: ${producto["descripcion"]}", 50f, 320f, textPaint)

            pdfDocument.finishPage(page)

            try {
                pdfDocument.writeTo(ParcelFileDescriptor.AutoCloseOutputStream(destination))
                callback.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
            } finally {
                pdfDocument.close()
            }
        }
    }

    printManager.print("Barcode_Print", printAdapter, null)
}

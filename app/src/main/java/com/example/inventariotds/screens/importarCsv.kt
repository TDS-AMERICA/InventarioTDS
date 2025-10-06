package com.example.inventariotds.screens

/*
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventariotds.model.Producto
import com.example.inventariotds.viewmodel.InventarioViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

fun importarCsv(uri: Uri, context: Context, viewModel: InventarioViewModel) {
    try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val productos = mutableListOf<Producto>()

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val lines = reader.readLines()
            if (lines.isEmpty()) {
                Toast.makeText(context, "El archivo CSV está vacío", Toast.LENGTH_SHORT).show()
                return
            }

            lines.drop(1).forEachIndexed { index, line ->
                val values = line.split(";").map { it.trim() }

                if (values.size < 11) {
                    Toast.makeText(context, "Error en línea ${index + 2}: Datos incompletos", Toast.LENGTH_SHORT).show()
                    return@forEachIndexed
                }

                val producto = Producto(
                    ubicacion = values.getOrNull(0) ?: "",
                    codigo = values.getOrNull(1) ?: "",
                    descripcion = values.getOrNull(2) ?: "",
                    cantidadContada = values.getOrNull(3)?.toIntOrNull() ?: 0,
                    lote = values.getOrNull(4),
                    serie = values.getOrNull(5),
                    fechaVencimiento = values.getOrNull(6),
                    fechaFabricacion = values.getOrNull(7),
                    bulto = values.getOrNull(8),
                    observacion = values.getOrNull(9),
                    partNumber = values.getOrNull(10),
                    fueContado = false
                )

                if (producto.codigo.isNotBlank()) {
                    productos.add(producto)
                }
            }
        }

        viewModel.importarDesdeCSV(productos)
        Toast.makeText(context, "CSV importado correctamente", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Error al importar CSV: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}



 */


import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.inventariotds.model.Producto
import com.example.inventariotds.util.guardarProductosMaestro
import com.example.inventariotds.viewmodel.InventarioViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

fun importarCsv(uri: Uri, context: Context, viewModel: InventarioViewModel) {
    try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val productos = mutableListOf<Producto>()
        var errores = 0

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val lines = reader.readLines().filter { it.isNotBlank() }
            if (lines.isEmpty()) {
                Toast.makeText(context, "El archivo CSV está vacío", Toast.LENGTH_SHORT).show()
                return
            }

            // Detectar el separador automáticamente
            val sep = when {
                lines.first().contains(",") && lines.first().split(",").size > 2 -> ","
                lines.first().contains(";") && lines.first().split(";").size > 2 -> ";"
                else -> {
                    Toast.makeText(context, "No se pudo detectar el separador del archivo CSV", Toast.LENGTH_LONG).show()
                    return
                }
            }

            lines.drop(1).forEachIndexed { index, line ->
                if (line.isBlank()) return@forEachIndexed
                val values = line.split(sep).map { it.trim() }

                // Usa solo los índices esperados: 0=internalCode, 1=codigo_barra, 2=descripcion, resto igual
                val internalCode = values.getOrNull(0) ?: ""
                val codigoBarra = values.getOrNull(1) ?: ""
                val descripcion = values.getOrNull(2) ?: ""

                if (internalCode.isBlank() || codigoBarra.isBlank() || descripcion.isBlank()) {
                    errores++
                    return@forEachIndexed
                }

                val producto = Producto(
                    internalCode = internalCode,
                    codigo = codigoBarra,
                    descripcion = descripcion,
                    cantidadContada = values.getOrNull(3)?.toIntOrNull() ?: 0,
                    ubicacion = values.getOrNull(4) ?: "",
                    lote = values.getOrNull(5),
                    serie = values.getOrNull(6),
                    fechaVencimiento = values.getOrNull(7),
                    fechaFabricacion = values.getOrNull(8),
                    bulto = values.getOrNull(9),
                    observacion = values.getOrNull(10),
                    partNumber = values.getOrNull(11),
                    fueContado = false
                    // Agrega más si tu modelo tiene más campos
                )

                productos.add(producto)
            }
        }

        viewModel.importarDesdeCSV(productos)
        guardarProductosMaestro(context, productos)

        Toast.makeText(
            context,
            "CSV importado: ${productos.size} productos. ${if (errores > 0) "$errores líneas ignoradas." else ""}",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al importar CSV: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

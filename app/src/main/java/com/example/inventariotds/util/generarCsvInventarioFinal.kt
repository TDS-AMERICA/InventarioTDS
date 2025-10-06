package com.example.inventariotds.util
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.inventariotds.model.Producto
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Parte 1
fun generarCsvInventarioFinal(context: Context, productos: List<Producto>): File? {
    val appDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    if (appDirectory == null) {
        Toast.makeText(context, "No se puede acceder al almacenamiento", Toast.LENGTH_SHORT).show()
        return null
    }

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = sdf.format(Date())
    val fileName = "Inventario_$timestamp.csv"
    val file = File(appDirectory, fileName)

    try {
        FileWriter(file).use { writer ->
            writer.append("Código interno,Código de barras,Descripción,Ubicación,Cantidad,Lote,Serie,Fecha Vencimiento,Fecha Fabricación,Bulto,Observación,Part Number,Fue contado,Fecha de ingreso,Responsable,Reconteo1,Reconteo2,Reconteo3,Reconteo4,Reconteo5\n")
            productos.forEach { p ->
                writer.appendLine(listOf(
                    p.internalCode ?: "NULL",
                    p.codigo,
                    p.descripcion,
                    p.ubicacion,
                    p.cantidadContada.toString(),
                    p.lote ?: "NULL",
                    p.serie ?: "NULL",
                    p.fechaVencimiento ?: "NULL",
                    p.fechaFabricacion ?: "NULL",
                    p.bulto ?: "NULL",
                    p.observacion ?: "NULL",
                    p.partNumber ?: "NULL",
                    p.fueContado.toString(),
                    p.fechaIngreso ?: "NULL",
                    p.responsable ?: "NULL",
                    p.reconteo1?.toString() ?: "NULL",
                    p.reconteo2?.toString() ?: "NULL",
                    p.reconteo3?.toString() ?: "NULL",
                    p.reconteo4?.toString() ?: "NULL",
                    p.reconteo5?.toString() ?: "NULL"
                ).joinToString(","))
            }
        }
        Toast.makeText(context, "CSV generado: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error al generar CSV", Toast.LENGTH_SHORT).show()
        return null
    }
}

// Parte 2
fun shareCsv(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // importante: debes tener <provider> en AndroidManifest.xml
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Archivo de Inventario")
        putExtra(Intent.EXTRA_TEXT, "Adjunto archivo CSV generado.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Compartir CSV vía..."))
}



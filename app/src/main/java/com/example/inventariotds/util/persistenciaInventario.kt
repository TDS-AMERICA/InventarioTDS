package com.example.inventariotds.util

import android.content.Context
import com.example.inventariotds.model.Producto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*
fun guardarProductosInventariados(context: Context, productos: List<Producto>) {
    val prefs = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val responsable = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
        .getString("responsable", "jc") ?: "jc"
    val set = productos.map { p ->
        val fechaIngreso = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        listOfNotNull(
            "Código interno:${p.internalCode ?: ""}",
            "Código de barras:${p.codigo}",
            "Descripción:${p.descripcion}",
            "Ubicación:${p.ubicacion}",
            if (p.cantidadContada > 0) "Cantidad:${p.cantidadContada}" else null,
            p.lote?.let { "Lote:$it" },
            p.serie?.let { "Serie:$it" },
            p.fechaVencimiento?.let { "Fecha de vencimiento:$it" },
            p.fechaFabricacion?.let { "Fecha de fabricación:$it" },
            p.bulto?.let { "Bulto:$it" },
            p.observacion?.let { "Observación:$it" },
            p.partNumber?.let { "Part Number:$it" },
            "Fue contado:${p.fueContado}",
            "Fecha de ingreso:$fechaIngreso",
            "Responsable:$responsable"
        ).joinToString(";")
    }.toSet()

    editor.putStringSet("inventario", set)
    editor.apply()
}


fun cargarProductosInventariados(context: Context): List<Producto> {
    val prefs = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val set = prefs.getStringSet("inventario", emptySet()) ?: emptySet()

    return set.mapNotNull { line ->
        val v = line.split(";")
        if (v.size < 13) return@mapNotNull null
        Producto(
            internalCode = v[0].removePrefix("Código interno:"),
            codigo = v[1].removePrefix("Código de barras:"),
            descripcion = v[2].removePrefix("Descripción:"),
            ubicacion = v[3].removePrefix("Ubicación:"),
            cantidadContada = v[4].removePrefix("Cantidad:").toIntOrNull() ?: 0,
            lote = v[5].removePrefix("Lote:").ifBlank { null },
            serie = v[6].removePrefix("Serie:").ifBlank { null },
            fechaVencimiento = v[7].removePrefix("Fecha de vencimiento:").ifBlank { null },
            fechaFabricacion = v[8].removePrefix("Fecha de fabricación:").ifBlank { null },
            bulto = v[9].removePrefix("Bulto:").ifBlank { null },
            observacion = v[10].removePrefix("Observación:").ifBlank { null },
            partNumber = v[11].removePrefix("Part Number:").ifBlank { null },
            fueContado = v[12].removePrefix("Fue contado:").toBoolean()
            // Los campos "Fecha de ingreso" y "Responsable" no se usan en el modelo Producto
        )
    }
}


 */

fun guardarProductosInventariados(context: Context, productos: List<Producto>) {
    val prefs = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(productos)
    editor.putString("inventario_lista", json)
    editor.apply()
}

fun cargarProductosInventariados(context: Context): List<Producto> {
    val prefs = context.getSharedPreferences("inventariados", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("inventario_lista", null)
    return if (json != null) {
        val type = object : TypeToken<List<Producto>>() {}.type
        gson.fromJson(json, type)
    } else {
        emptyList()
    }
}

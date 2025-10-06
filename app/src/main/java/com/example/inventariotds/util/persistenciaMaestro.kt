package com.example.inventariotds.util

import android.content.Context
import com.example.inventariotds.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
/*
fun guardarProductosMaestro(context: Context, productos: List<Producto>) {
    val prefs = context.getSharedPreferences("maestro", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    val set = productos.map { p ->
        listOf(
            p.ubicacion,
            p.codigo,
            p.descripcion,
            p.cantidadContada.toString(),
            p.lote ?: "",
            p.serie ?: "",
            p.fechaVencimiento ?: "",
            p.fechaFabricacion ?: "",
            p.bulto ?: "",
            p.observacion ?: "",
            p.partNumber ?: "",
            p.fueContado.toString()
        ).joinToString(";")
    }.toSet()

    editor.putStringSet("productos", set)
    editor.apply()
}

fun cargarProductosMaestro(context: Context): List<Producto> {
    val prefs = context.getSharedPreferences("maestro", Context.MODE_PRIVATE)
    val set = prefs.getStringSet("productos", emptySet()) ?: emptySet()

    return set.mapNotNull { line ->
        val v = line.split(";")
        if (v.size < 12) return@mapNotNull null
        Producto(
            ubicacion = v[0],
            codigo = v[1],
            descripcion = v[2],
            cantidadContada = v[3].toIntOrNull() ?: 0,
            lote = v[4].ifBlank { null },
            serie = v[5].ifBlank { null },
            fechaVencimiento = v[6].ifBlank { null },
            fechaFabricacion = v[7].ifBlank { null },
            bulto = v[8].ifBlank { null },
            observacion = v[9].ifBlank { null },
            partNumber = v[10].ifBlank { null },
            fueContado = v[11].toBoolean()
        )
    }
}


 */

fun guardarProductosMaestro(context: Context, productos: List<Producto>) {
    val prefs = context.getSharedPreferences("maestro", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(productos)
    editor.putString("productos_json", json)
    editor.apply()
}

fun cargarProductosMaestro(context: Context): List<Producto> {
    val prefs = context.getSharedPreferences("maestro", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("productos_json", null)
    if (json.isNullOrBlank()) return emptyList()
    val type = object : TypeToken<List<Producto>>() {}.type
    return gson.fromJson(json, type) ?: emptyList()
}


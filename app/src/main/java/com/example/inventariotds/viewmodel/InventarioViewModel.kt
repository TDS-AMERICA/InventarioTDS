package com.example.inventariotds.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.inventariotds.model.Producto
import com.example.inventariotds.util.*

class InventarioViewModel : ViewModel() {
    val productosMaestro = mutableStateListOf<Producto>()
    val productosInventariados = mutableStateListOf<Producto>()

    fun importarDesdeCSV(lista: List<Producto>) {
        productosMaestro.clear()
        productosMaestro.addAll(lista)
    }

    fun registrarConteo(codigo: String, cantidad: Int) {
        val existente = productosMaestro.find { it.codigo == codigo }
        if (existente != null) {
            existente.fueContado = true
            existente.cantidadContada = cantidad

            productosInventariados.removeAll { it.codigo == codigo }
            productosInventariados.add(existente)
        } else {
            val nuevo = Producto(
                codigo = codigo,
                descripcion = "Manual",
                ubicacion = "",
                cantidadContada = cantidad,
                fueContado = true
            )
            productosMaestro.add(nuevo)
            productosInventariados.add(nuevo)
        }
    }

    fun persistirMaestro(context: Context) {
        guardarProductosMaestro(context, productosMaestro)
    }

    fun registrarConteoManual(
        codigo: String,
        descripcion: String,
        ubicacion: String,
        cantidad: Int
    ) {
        val existente = productosMaestro.find { it.codigo == codigo }

        if (existente != null) {
            existente.fueContado = true
            existente.cantidadContada = cantidad
            productosInventariados.removeAll { it.codigo == codigo }
            productosInventariados.add(existente)
        } else {
            val nuevo = Producto(
                codigo = codigo,
                descripcion = descripcion,
                ubicacion = ubicacion,
                cantidadContada = cantidad,
                fueContado = true
            )
            productosMaestro.add(nuevo)
            productosInventariados.add(nuevo)
        }
    }

    // === REGISTRO MANUAL: agrega SIEMPRE un nuevo registro en inventariados ===
    fun registrarProductoManual(map: Map<String, String>, responsable: String, context: Context) {
        val codigo = map["Código de barras"] ?: ""
        val internalCode = map["Código interno"] ?: ""
        val descripcion = map["Descripción"] ?: "Manual"
        val ubicacion = map["Ubicación"] ?: ""
        val cantidad = map["Cantidad"]?.toIntOrNull() ?: 0

        // ----- 1. Actualizar o agregar en MAESTRO
        val idxMaestro = productosMaestro.indexOfFirst { it.codigo == codigo && it.internalCode == internalCode }
        if (idxMaestro != -1) {
            val previo = productosMaestro[idxMaestro]
            productosMaestro[idxMaestro] = previo.copy(
                cantidadContada = previo.cantidadContada + cantidad,
                fueContado = true,
                responsable = responsable,
                ubicacion = if (ubicacion.isNotBlank()) ubicacion else previo.ubicacion
            )
        } else {
            val producto = Producto(
                internalCode = internalCode,
                ubicacion = ubicacion,
                codigo = codigo,
                descripcion = descripcion,
                cantidadContada = cantidad,
                lote = map["Lote"],
                serie = map["Serie"],
                fechaVencimiento = map["Fecha de vencimiento"],
                fechaFabricacion = map["Fecha de fabricación"],
                bulto = map["Bulto"],
                observacion = map["Observación"],
                partNumber = map["Part Number"],
                responsable = responsable,
                fueContado = true
            )
            productosMaestro.add(producto)
        }

        // ----- 2. SIEMPRE agrega en inventariados (no filtra, no reemplaza)
        val productoInventariado = Producto(
            internalCode = internalCode,
            ubicacion = ubicacion,
            codigo = codigo,
            descripcion = descripcion,
            cantidadContada = cantidad,
            lote = map["Lote"],
            serie = map["Serie"],
            fechaVencimiento = map["Fecha de vencimiento"],
            fechaFabricacion = map["Fecha de fabricación"],
            bulto = map["Bulto"],
            observacion = map["Observación"],
            partNumber = map["Part Number"],
            responsable = responsable,
            fueContado = true
        )
        productosInventariados.add(productoInventariado)

        // ----- 3. Guardar lista completa (NO llamar cargarInventariados aquí)
        persistirInventariados(context)
    }

    fun registrarProductoBarrido(map: Map<String, String>, responsable: String, context: Context) {
        val codigo = map["Código de barras"] ?: ""
        val internalCode = map["Código interno"] ?: ""
        val descripcion = map["Descripción"] ?: "Manual"
        val ubicacion = map["Ubicación"] ?: ""
        val cantidad = 1

        val idxInventariado = productosInventariados.indexOfFirst {
            it.codigo == codigo &&
                    it.internalCode == internalCode &&
                    it.ubicacion == ubicacion
        }

        if (idxInventariado != -1) {
            val previo = productosInventariados[idxInventariado]
            productosInventariados[idxInventariado] = previo.copy(
                cantidadContada = previo.cantidadContada + cantidad,
                fueContado = true,
                responsable = responsable
            )
        } else {
            val producto = Producto(
                internalCode = internalCode,
                ubicacion = ubicacion,
                codigo = codigo,
                descripcion = descripcion,
                cantidadContada = cantidad,
                lote = map["Lote"],
                serie = map["Serie"],
                fechaVencimiento = map["Fecha de vencimiento"],
                fechaFabricacion = map["Fecha de fabricación"],
                bulto = map["Bulto"],
                observacion = map["Observación"],
                partNumber = map["Part Number"],
                responsable = responsable,
                fueContado = true
            )
            productosInventariados.add(producto)
        }

        // Maestro: marca como contado o agrega si es nuevo
        val idxMaestro = productosMaestro.indexOfFirst {
            it.codigo == codigo && it.internalCode == internalCode
        }
        if (idxMaestro != -1) {
            val previo = productosMaestro[idxMaestro]
            productosMaestro[idxMaestro] = previo.copy(
                fueContado = true,
                cantidadContada = previo.cantidadContada + cantidad,
                responsable = responsable,
                ubicacion = if (ubicacion.isNotBlank()) ubicacion else previo.ubicacion
            )
        } else {
            // Si no existe, se agrega al maestro también
            val producto = Producto(
                internalCode = internalCode,
                ubicacion = ubicacion,
                codigo = codigo,
                descripcion = descripcion,
                cantidadContada = cantidad,
                lote = map["Lote"],
                serie = map["Serie"],
                fechaVencimiento = map["Fecha de vencimiento"],
                fechaFabricacion = map["Fecha de fabricación"],
                bulto = map["Bulto"],
                observacion = map["Observación"],
                partNumber = map["Part Number"],
                responsable = responsable,
                fueContado = true
            )
            productosMaestro.add(producto)
        }

        persistirInventariados(context)
        persistirMaestro(context)
    }


    // ==== UTILIDADES ====
    fun cargarMaestro(context: Context) {
        val cargados = cargarProductosMaestro(context)
        productosMaestro.clear()
        productosMaestro.addAll(cargados)
    }

    fun persistirInventariados(context: Context) {
        guardarProductosInventariados(context, productosInventariados)
    }

    fun cargarInventariados(context: Context) {
        val cargados = cargarProductosInventariados(context)
        productosInventariados.clear()
        productosInventariados.addAll(cargados)
    }

    fun obtenerCantidadTotal(): Int {
        return productosInventariados.sumOf { it.cantidadContada }
    }

    fun obtenerPorUbicacion(): Map<String, Int> {
        return productosInventariados.groupBy { it.ubicacion }
            .mapValues { entry -> entry.value.sumOf { it.cantidadContada } }
    }

    fun buscarEnInventario(query: String): List<Producto> {
        return productosInventariados.filter {
            it.codigo.contains(query, ignoreCase = true)
        }
    }

    // Si necesitas más lógica de comparación puedes usar esto:
    private fun encontrarProductoSimilar(lista: List<Producto>, nuevo: Producto): Producto? {
        return lista.find {
            it.codigo == nuevo.codigo &&
                    it.descripcion == nuevo.descripcion &&
                    it.ubicacion == nuevo.ubicacion &&
                    it.internalCode == nuevo.internalCode &&
                    it.lote == nuevo.lote &&
                    it.bulto == nuevo.bulto
        }
    }
}

package com.example.inventariotds.model

data class Producto(
    val codigo: String,
    val descripcion: String,
    val ubicacion: String = "",
    val lote: String? = null,
    val serie: String? = null,
    val fechaVencimiento: String? = null,
    val fechaFabricacion: String? = null,
    val bulto: String? = null,
    val partNumber: String? = null,
    val observacion: String? = null,
    var fueContado: Boolean = false,
    var cantidadContada: Int = 0,
    val internalCode: String? = null,

//COSAS DE LA IMPORTACION
    var fechaIngreso: String? = null,
    var responsable: String? = null,
    var reconteo1: Int? = null,
    var reconteo2: Int? = null,
    var reconteo3: Int? = null,
    var reconteo4: Int? = null,
    var reconteo5: Int? = null

)

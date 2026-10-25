package com.tunombre.pulperia

data class Producto(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int,
    val barcode: String = "",
    val minStock: Int = 5
) {
    val valor get() = price * stock

    override fun toString(): String {
        val cb = if (barcode.isNotEmpty()) "  |  CB: $barcode" else ""
        return "%s  |  L %.2f  |  Stock: %d%s".format(name, price, stock, cb)
    }
}

package com.tunombre.pulperia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(ctx: Context) : SQLiteOpenHelper(ctx, "pulperia.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE products(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                stock INTEGER NOT NULL,
                barcode TEXT,
                min_stock INTEGER NOT NULL DEFAULT 5)"""
        )
        db.execSQL(
            """CREATE TABLE movements(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL,
                qty INTEGER NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE)"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movements")
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }


    private fun valores(p: Producto) = ContentValues().apply {
        put("name", p.name)
        put("price", p.price)
        put("stock", p.stock)
        put("barcode", p.barcode)
        put("min_stock", p.minStock)
    }

    fun insertar(p: Producto): Long = writableDatabase.insert("products", null, valores(p))

    fun actualizar(p: Producto): Int =
        writableDatabase.update("products", valores(p), "id=?", arrayOf(p.id.toString()))

    fun eliminar(id: Int): Int =
        writableDatabase.delete("products", "id=?", arrayOf(id.toString()))

    fun listar(): List<Producto> {
        val lista = mutableListOf<Producto>()
        readableDatabase.rawQuery(
            "SELECT id,name,price,stock,barcode,min_stock FROM products ORDER BY name", null
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Producto(
                        c.getInt(0), c.getString(1), c.getDouble(2),
                        c.getInt(3), c.getString(4) ?: "", c.getInt(5)
                    )
                )
            }
        }
        return lista
    }

    fun obtener(id: Int): Producto? = listar().firstOrNull { it.id == id }



    fun registrarMovimiento(productoId: Int, qty: Int, tipo: String): String? {
        val db = writableDatabase
        val p = obtener(productoId) ?: return "Producto no encontrado"
        val nuevoStock = if (tipo == "ENTRADA") p.stock + qty else p.stock - qty
        if (nuevoStock < 0) return "Stock insuficiente (disponible: ${p.stock})"

        db.beginTransaction()
        try {
            db.execSQL("UPDATE products SET stock=? WHERE id=?", arrayOf<Any>(nuevoStock, productoId))
            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            db.insert("movements", null, ContentValues().apply {
                put("product_id", productoId)
                put("qty", qty)
                put("type", tipo)
                put("date", fecha)
            })
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return null
    }


    fun valorTotal(): Double {
        readableDatabase.rawQuery("SELECT COALESCE(SUM(price*stock),0) FROM products", null)
            .use { c ->
                c.moveToFirst()
                return c.getDouble(0)
            }
    }

    fun top5(): List<Producto> = listar().sortedByDescending { it.valor }.take(5)

    fun stockBajo(): List<Producto> = listar().filter { it.stock <= it.minStock }
}

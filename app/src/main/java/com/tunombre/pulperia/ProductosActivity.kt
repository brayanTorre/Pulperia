package com.tunombre.pulperia

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProductosActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var lv: ListView
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var etMin: EditText
    private lateinit var etBarcode: EditText
    private var editandoId = 0
    private var productos = listOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        db = DBHelper(this)
        lv = findViewById(R.id.lvProductos)
        etNombre = findViewById(R.id.etNombre)
        etPrecio = findViewById(R.id.etPrecio)
        etStock = findViewById(R.id.etStock)
        etMin = findViewById(R.id.etMinStock)
        etBarcode = findViewById(R.id.etBarcode)

        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardar() }
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener { limpiar() }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        lv.setOnItemClickListener { _, _, pos, _ ->
            val p = productos[pos]
            editandoId = p.id
            etNombre.setText(p.name)
            etPrecio.setText(p.price.toString())
            etStock.setText(p.stock.toString())
            etMin.setText(p.minStock.toString())
            etBarcode.setText(p.barcode)
        }
        lv.setOnItemLongClickListener { _, _, pos, _ ->
            val p = productos[pos]
            AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar ${p.name} y sus movimientos?")
                .setPositiveButton("Sí") { _, _ ->
                    db.eliminar(p.id)
                    limpiar()
                    refrescar()
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
        refrescar()
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        val precio = etPrecio.text.toString().toDoubleOrNull()
        val stock = etStock.text.toString().toIntOrNull()
        val min = etMin.text.toString().toIntOrNull() ?: 5
        if (nombre.isEmpty() || precio == null || stock == null) {
            Toast.makeText(this, "Completa nombre, precio y stock", Toast.LENGTH_SHORT).show()
            return
        }
        val p = Producto(editandoId, nombre, precio, stock, etBarcode.text.toString().trim(), min)
        if (editandoId == 0) db.insertar(p) else db.actualizar(p)
        limpiar()
        refrescar()
    }

    private fun limpiar() {
        editandoId = 0
        listOf(etNombre, etPrecio, etStock, etMin, etBarcode).forEach { it.text.clear() }
    }

    private fun refrescar() {
        productos = db.listar()
        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, productos)
    }
}

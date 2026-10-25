package com.tunombre.pulperia

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MovimientosActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var sp: Spinner
    private var productos = listOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movimientos)


        db = DBHelper(this)
        sp = findViewById(R.id.spProducto)
        cargarSpinner()
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            if (productos.isEmpty()) {
                Toast.makeText(this, "Primero registra productos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val etCantidad = findViewById<EditText>(R.id.etCantidad)
            val qty = etCantidad.text.toString().toIntOrNull()
            if (qty == null || qty <= 0) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val tipo = if (findViewById<RadioButton>(R.id.rbEntrada).isChecked) "ENTRADA" else "SALIDA"
            val producto = productos[sp.selectedItemPosition]

            val error = db.registrarMovimiento(producto.id, qty, tipo)
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Movimiento registrado", Toast.LENGTH_SHORT).show()
            etCantidad.text.clear()


            val actualizado = db.obtener(producto.id)
            if (actualizado != null && actualizado.stock <= actualizado.minStock) {
                AlertDialog.Builder(this)
                    .setTitle("⚠ Stock bajo")
                    .setMessage(
                        "${actualizado.name} tiene ${actualizado.stock} unidades " +
                                "(mínimo: ${actualizado.minStock}). Es hora de reabastecer."
                    )
                    .setPositiveButton("Entendido", null)
                    .show()
            }
            cargarSpinner()
        }
    }

    private fun cargarSpinner() {
        val pos = sp.selectedItemPosition
        productos = db.listar()
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, productos)
        if (pos in productos.indices) sp.setSelection(pos)
    }
}

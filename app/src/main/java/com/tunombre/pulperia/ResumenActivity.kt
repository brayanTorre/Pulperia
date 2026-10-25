package com.tunombre.pulperia

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ResumenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen)
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
        val db = DBHelper(this)

        findViewById<TextView>(R.id.tvTotal).text =
            "Valor total del inventario: L %.2f".format(db.valorTotal())

        val top = db.top5().map { "%s  →  L %.2f".format(it.name, it.valor) }
        findViewById<ListView>(R.id.lvTop).adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, top)

        val bajo = db.stockBajo().map { "${it.name}: ${it.stock} (mín. ${it.minStock})" }
        findViewById<ListView>(R.id.lvBajo).adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, bajo.ifEmpty { listOf("Sin alertas") })

    }
}

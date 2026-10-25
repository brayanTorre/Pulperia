package com.tunombre.pulperia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnProductos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<Button>(R.id.btnMovimientos).setOnClickListener {
            startActivity(Intent(this, MovimientosActivity::class.java))
        }
        findViewById<Button>(R.id.btnResumen).setOnClickListener {
            startActivity(Intent(this, ResumenActivity::class.java))
        }
    }
}

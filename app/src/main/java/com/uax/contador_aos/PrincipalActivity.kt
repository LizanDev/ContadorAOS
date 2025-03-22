package com.uax.contador_aos

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase



class PrincipalActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_principal)

        database = FirebaseDatabase.getInstance()

        val spinnerEjercito1: Spinner = findViewById(R.id.spinnerEjercito1)
        val spinnerEjercito2: Spinner = findViewById(R.id.spinnerEjercito2)
        val editTextJugador1: EditText = findViewById(R.id.editTextJugador1)
        val editTextJugador2: EditText = findViewById(R.id.editTextJugador2)
        val buttonContinuar: Button = findViewById(R.id.buttonContinuar)
        val buttonHistorial: Button = findViewById(R.id.buttonHistorial)


        // Configuro los Spinner con los ejércitos
        val ejercitos = resources.getStringArray(R.array.ejercitos)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ejercitos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEjercito1.adapter = adapter
        spinnerEjercito2.adapter = adapter

        // Configuro el botón para continuar
        buttonContinuar.setOnClickListener {
            val nombreJugador1 = editTextJugador1.text.toString()
            val nombreJugador2 = editTextJugador2.text.toString()

            // aseguramos que los campos no esten vacios
            if (nombreJugador1.isEmpty() || nombreJugador2.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese nombres para ambos jugadores", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ejercitoJugador1 = spinnerEjercito1.selectedItem.toString()
            val ejercitoJugador2 = spinnerEjercito2.selectedItem.toString()

            // Pasar los datos a MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("JUGADOR1_NOMBRE", nombreJugador1)
                putExtra("JUGADOR1_EJERCITO", ejercitoJugador1)
                putExtra("JUGADOR2_NOMBRE", nombreJugador2)
                putExtra("JUGADOR2_EJERCITO", ejercitoJugador2)
            }
            startActivity(intent)
        }

        buttonHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }
}
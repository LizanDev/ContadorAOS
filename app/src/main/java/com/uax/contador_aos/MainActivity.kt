package com.uax.contador_aos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.uax.contador_aos.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding

    // Player information
    private var jugador1Nombre: String = ""
    private var jugador1Ejercito: String = ""
    private var jugador2Nombre: String = ""
    private var jugador2Ejercito: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve player information from intent
        jugador1Nombre = intent.getStringExtra("JUGADOR1_NOMBRE") ?: ""
        jugador1Ejercito = intent.getStringExtra("JUGADOR1_EJERCITO") ?: ""
        jugador2Nombre = intent.getStringExtra("JUGADOR2_NOMBRE") ?: ""
        jugador2Ejercito = intent.getStringExtra("JUGADOR2_EJERCITO") ?: ""

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        binding.sumar1.setOnClickListener(this)
        binding.sumar2.setOnClickListener(this)
        binding.sumar5.setOnClickListener(this)
        binding.sumar1enemy.setOnClickListener(this)
        binding.sumar2enemy.setOnClickListener(this)
        binding.sumar5enemy.setOnClickListener(this)
        binding.commandplus1.setOnClickListener(this)
        binding.commandplus1enemy.setOnClickListener(this)
        binding.commandminus1.setOnClickListener(this)
        binding.commandminus1enemy.setOnClickListener(this)
        binding.reset.setOnClickListener(this)
        binding.resetenemy.setOnClickListener(this)

        binding.saveGameButton.setOnClickListener {
            saveGameResultToFirebase()
        }
    }

    private fun resetear(view: View) {
        when (view.id) {
            binding.reset.id -> binding.Contador1.text = "0"
            binding.resetenemy.id -> binding.Contador2.text = "0"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.reset.id -> resetear(v)
            binding.resetenemy.id -> resetear(v)
            binding.sumar1.id -> binding.Contador1.text =
                (binding.Contador1.text.toString().toInt() + 1).toString()
            binding.sumar2.id -> binding.Contador1.text =
                (binding.Contador1.text.toString().toInt() + 2).toString()
            binding.sumar5.id -> binding.Contador1.text =
                (binding.Contador1.text.toString().toInt() + 5).toString()
            binding.sumar1enemy.id -> binding.Contador2.text =
                (binding.Contador2.text.toString().toInt() + 1).toString()
            binding.sumar2enemy.id -> binding.Contador2.text =
                (binding.Contador2.text.toString().toInt() + 2).toString()
            binding.sumar5enemy.id -> binding.Contador2.text =
                (binding.Contador2.text.toString().toInt() + 5).toString()
            binding.commandplus1.id -> binding.combatpoints1.text =
                (binding.combatpoints1.text.toString().toInt() + 1).toString()
            binding.commandplus1enemy.id -> binding.combarpoints2.text =
                (binding.combarpoints2.text.toString().toInt() + 1).toString()
            binding.commandminus1.id -> binding.combatpoints1.text =
                (binding.combatpoints1.text.toString().toInt() - 1).toString()
            binding.commandminus1enemy.id -> binding.combarpoints2.text =
                (binding.combarpoints2.text.toString().toInt() - 1).toString()
        }
        v?.let { animarBoton(it) }
    }

    private fun animarBoton(view: View) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100)
        }
    }

    // Function to save game result to Firebase
    fun saveGameResultToFirebase() {
        try {
            val database = FirebaseDatabase.getInstance()
            val historialRef = database.getReference("historial")

            // Get current points for both players
            val puntosJugador1 = binding.Contador1.text.toString().toInt()
            val puntosJugador2 = binding.Contador2.text.toString().toInt()

            // Determine the winner based on points
            val ganador = if (puntosJugador1 > puntosJugador2) jugador1Nombre else jugador2Nombre

            // Get current date and time
            val fechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // Create the data object to save
            val gameData = HistorialData(
                jugador1Nombre = jugador1Nombre,
                jugador2Nombre = jugador2Nombre,
                jugador1Ejercito = jugador1Ejercito,
                jugador2Ejercito = jugador2Ejercito,
                fechaHora = fechaHora,
                ganador = ganador,
                puntosJugador1 = puntosJugador1,
                puntosJugador2 = puntosJugador2
            )

            // Save to Firebase
            historialRef.push().setValue(gameData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Partida guardada en el historial", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar la partida: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}




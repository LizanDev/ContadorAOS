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
    private lateinit var selectedArmy1: String
    private lateinit var selectedArmy2: String

    // Informacion de los jugadores
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

        // datos de los jugadores que pasaremos a la siguiente pantalla
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
            binding.sumar1.id -> {
                binding.Contador1.text = (binding.Contador1.text.toString().toInt() + 1).toString()
                updateBackground()
            }

            binding.sumar2.id -> {
                binding.Contador1.text = (binding.Contador1.text.toString().toInt() + 2).toString()
                updateBackground()
            }

            binding.sumar5.id -> {
                binding.Contador1.text = (binding.Contador1.text.toString().toInt() + 5).toString()
                updateBackground()
            }

            binding.sumar1enemy.id -> {
                binding.Contador2.text = (binding.Contador2.text.toString().toInt() + 1).toString()
                updateBackground()
            }

            binding.sumar2enemy.id -> {
                binding.Contador2.text = (binding.Contador2.text.toString().toInt() + 2).toString()
                updateBackground()
            }

            binding.sumar5enemy.id -> {
                binding.Contador2.text = (binding.Contador2.text.toString().toInt() + 5).toString()
                updateBackground()
            }

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

    private val armyImages = mapOf(
        "Forjados en la tormenta" to R.drawable.forjados_en_la_tormenta,
        "Clanes Orruks" to R.drawable.clanes_orruks,
        "Esclavos de la oscuridad" to R.drawable.esclavos_de_la_oscuridad,
        "Lumineth soberanos" to R.drawable.lumineth_soberanos,
        "Hijos de Behemat" to R.drawable.hijos_de_behemat,
        "Agusanados de Nurgle" to R.drawable.agusanados_de_nuegle,
        "Masticatribus Ogors" to R.drawable.mastcatribus_ogors,
        "Necroseñores Pudrealmas" to R.drawable.necroseniores_pudrealmas,
        "Tipejoz nokturnoz" to R.drawable.tipejoz_nokturnoz,
        "Seraphones" to R.drawable.seraphon,
        "Profundos Idoneth" to R.drawable.profundos_idoneth,
        "Noctánimas" to R.drawable.noctanimas,
        "Sylvaneth" to R.drawable.sylvaneth,
        "Discípulos de Tzeentch" to R.drawable.discipulos_de_tzeentch,
        "Osiarcas Cosechahuesos" to R.drawable.osiarcas_cosechahuesos,
        "Cortes Comecarne" to R.drawable.cortes_comecarne,
        "Hedonitas de Slaanesh" to R.drawable.hedonitas_de_slaanesh,
        "Filos de Khorne" to R.drawable.filos_de_khorne,
        "Altos Señores Kharadron" to R.drawable.altos_seniores_kharadron,
        "Ciudades de Sigmar" to R.drawable.ciudades_de_sigmar,
        "Matafuegos" to R.drawable.matafuegos,
        "Hijas de Khaine" to R.drawable.hijas_de_khaine,
        "Skaven" to R.drawable.skaven
    )

    // funcion que guarda los datos de la partida en firebase
    private fun saveGameResultToFirebase() {
        try {
            val database = FirebaseDatabase.getInstance()
            val historialRef = database.getReference("historial")

            // recogemos los puntos de cada jugador
            val puntosJugador1 = binding.Contador1.text.toString().toInt()
            val puntosJugador2 = binding.Contador2.text.toString().toInt()

            // indicamos el ganador segun la puntuacion
            val ganador = if (puntosJugador1 > puntosJugador2) {
                // asociao el ejercito elegido del jugador 1 con la imagen
                binding.root.setBackgroundResource(
                    armyImages[jugador1Ejercito] ?: R.mipmap.simbolonurgle
                ) // ponemos un background por defecto si no encuentra el ejercito
                jugador1Nombre
            } else {
                // lo mismo pero al jugador 2
                binding.root.setBackgroundResource(
                    armyImages[jugador2Ejercito] ?: R.mipmap.simbolonurgle
                )
                jugador2Nombre
            }

            // coge la fecha y hora actual del guardado
            val fechaHora =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // creamos un objeto con los datos de la partida
            val gameData = HistorialData(
                jugador1Nombre = jugador1Nombre,
                jugador2Nombre = jugador2Nombre,
                jugador1Ejercito = jugador1Ejercito,
                jugador2Ejercito = jugador2Ejercito,
                fechaHora = fechaHora,
                ganador = ganador,
                puntosJugador1 = puntosJugador1,
                puntosJugador2 = puntosJugador2,

            )

            // salvamos el objeto en firebase
            historialRef.push().setValue(gameData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Partida guardada en el historial", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al guardar la partida: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    //creamos la funcion que cambiará el background segun la puntuacion
    private fun updateBackground() {
        val puntosJugador1 = binding.Contador1.text.toString().toInt()
        val puntosJugador2 = binding.Contador2.text.toString().toInt()

        if (puntosJugador1 > puntosJugador2) {
            // asociamos el ejercito elegido del jugador 1 con la imagen
            binding.root.setBackgroundResource(armyImages[jugador1Ejercito] ?: R.mipmap.simbolonurgle) // Use army1_image as fallback
        } else if (puntosJugador2 > puntosJugador1) {
            // asicoamos el ejercito elegido del jugador 2 con la imagen
            binding.root.setBackgroundResource(armyImages[jugador2Ejercito] ?: R.mipmap.simbolonurgle) // Use army2_image as fallback
        } else {
            // en caso de empate ponemos un background por defecto
            binding.root.setBackgroundResource(R.mipmap.simbolonurgle) // Default background color
        }
    }
}




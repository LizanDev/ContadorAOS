package com.uax.contador_aos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

// creamso la clase de la base de datos
data class HistorialData(
    val jugador1Nombre: String = "",
    val jugador2Nombre: String = "",
    val jugador1Ejercito: String = "",
    val jugador2Ejercito: String = "",
    val fechaHora: String = "",
    val ganador: String = "",
    val puntosJugador1: Int = 0,
    val puntosJugador2: Int = 0
)

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private val historialList = mutableListOf<HistorialData>()
    private lateinit var emptyView: TextView
    private lateinit var loadingIndicator: View

    private val TAG = "HistorialActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        // configuro el padding de la vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // inicializamos las vistas
        recyclerView = findViewById(R.id.recyclerViewHistorial)
        emptyView = findViewById(R.id.emptyView)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        // configuro el recycler view
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistorialAdapter(historialList)
        recyclerView.adapter = adapter

        // cargamos los datos de firebase
        loadHistorialFromFirebase()
    }

    private fun loadHistorialFromFirebase() {
        try {
            // mostramos el indicador de carga
            loadingIndicator.visibility = View.VISIBLE
            emptyView.visibility = View.GONE

            val database = FirebaseDatabase.getInstance()
            val historialRef = database.getReference("historial")

            Log.d(TAG, "Cargamos datos desde Firebase")

            historialRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "Datos recibidos de Firebase, procesando...")
                    historialList.clear()

                    for (gameSnapshot in snapshot.children) {
                        try {
                            val gameData = gameSnapshot.getValue(HistorialData::class.java)
                            if (gameData != null) {
                                historialList.add(gameData)
                                Log.d(TAG, "Added game data: $gameData")
                            } else {
                                Log.w(TAG, "Received null game data for entry: ${gameSnapshot.key}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing game: ${e.message}")
                        }
                    }

                    // Sort by date (most recent first)
                    historialList.sortByDescending { it.fechaHora }

                    // Update the adapter
                    adapter.notifyDataSetChanged()

                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE

                    // Show empty view if no data
                    if (historialList.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        Log.d(TAG, "No se encontraron datos")
                    } else {
                        emptyView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        Log.d(TAG, "Displayed ${historialList.size} history items")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                    Log.e(TAG, "Firebase error: ${error.message}", error.toException())

                    Toast.makeText(
                        this@HistorialActivity,
                        "Error al cargar el historial: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error de conexion a Firebase: ${e.message}", e)
            loadingIndicator.visibility = View.GONE
            emptyView.visibility = View.VISIBLE

            Toast.makeText(
                this,
                "Error de conexión a Firebase: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

class HistorialAdapter(private val historialList: List<HistorialData>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creo las vistas del item de la lista
        val fechaTextView: TextView = itemView.findViewById(R.id.textViewFecha)
        val jugadoresTextView: TextView = itemView.findViewById(R.id.textViewJugadores)
        val resultadoTextView: TextView = itemView.findViewById(R.id.textViewResultado)
        val ganadorTextView: TextView = itemView.findViewById(R.id.textViewGanador)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = historialList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historialList[position]

        holder.fechaTextView.text = item.fechaHora
        holder.jugadoresTextView.text = "${item.jugador1Nombre} (${item.jugador1Ejercito}) vs ${item.jugador2Nombre} (${item.jugador2Ejercito})"
        holder.resultadoTextView.text = "${item.puntosJugador1} - ${item.puntosJugador2}"
        holder.ganadorTextView.text = "Ganador: ${item.ganador}"
    }
}



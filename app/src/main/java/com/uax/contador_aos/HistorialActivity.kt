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
import android.widget.ImageView

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
    
    // Mapeo de nombres de ejércitos a recursos de imágenes (igual que en MainActivity)
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creo las vistas del item de la lista
        val fechaTextView: TextView = itemView.findViewById(R.id.textViewFecha)
        val jugadoresTextView: TextView = itemView.findViewById(R.id.textViewJugadores)
        val resultadoTextView: TextView = itemView.findViewById(R.id.textViewResultado)
        val ganadorTextView: TextView = itemView.findViewById(R.id.textViewGanador)
        val imageViewArmy: ImageView = itemView.findViewById(R.id.imageViewArmy)
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
        
        // Determinar qué ejército ganó para mostrar su imagen
        val ejercitoGanador = if (item.ganador == item.jugador1Nombre) {
            item.jugador1Ejercito
        } else {
            item.jugador2Ejercito
        }
        
        // Usar el mapeo de imágenes para obtener el recurso correspondiente
        try {
            // Obtener el recurso de imagen del mapa usando el nombre del ejército ganador
            val resourceId = armyImages[ejercitoGanador]
            
            if (resourceId != null) {
                // Si se encuentra la imagen en el mapeo, mostrarla
                holder.imageViewArmy.setImageResource(resourceId)
                holder.imageViewArmy.visibility = View.VISIBLE
            } else {
                // Si no se encuentra en el mapeo, usar una imagen por defecto
                holder.imageViewArmy.setImageResource(R.mipmap.simbolonurgle)
                holder.imageViewArmy.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e("HistorialAdapter", "Error al cargar imagen: ${e.message}")
            // En caso de error, usar imagen por defecto en lugar de ocultar
            holder.imageViewArmy.setImageResource(R.mipmap.simbolonurgle)
            holder.imageViewArmy.visibility = View.VISIBLE
        }
    }
}



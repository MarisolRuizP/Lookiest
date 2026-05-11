package ruiz.marisol.lookiest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prendas")
data class PrendaRopa(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String = "",
    val nombre: String,
    val tienda: String = "",
    val talla: String,
    val color: String,
    val estampado: Boolean,
    val categoria: String,
    val tags: List<String> = emptyList(),
    val temporada: List<String> = emptyList(),
    val formalidad: String,
    val imagen: String? = null,
    val favorito: Boolean = false,
    val usadaHoy: Boolean = false
)
package ruiz.marisol.lookiest.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prendas")
data class PrendaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tienda: String = "",
    val talla: String,
    val color: String,
    val estampado: Boolean,
    val categoria: String,
    val tags: String = "",
    val temporada: String = "",
    val formalidad: String,
    val imagen: Int? = null,
    val favorito: Boolean = false,
    val usadaHoy: Boolean = false
)
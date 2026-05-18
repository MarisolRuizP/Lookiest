package ruiz.marisol.lookiest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class Outfit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String = "",
    val nombre: String = "",
    val prendas: String,
    val esPublico: Boolean = false,
    val etiquetas: String,
    val creadoPor: String = "Mi",
    val likes: Int? = 0,
    val favoritos: Int? = 0,
    val totalUsos: Int = 0,
    val esOutfitDeHoy: Boolean = false,
    val syncPendiente: Boolean = true,
    val firestoreId: String = ""
)
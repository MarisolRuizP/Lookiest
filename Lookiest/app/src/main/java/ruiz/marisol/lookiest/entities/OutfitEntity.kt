package ruiz.marisol.lookiest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String = "",
    val prendasIds: String = "",
    val esPublico: Boolean = false,
    val etiquetas: String = "",
    val creadoPor: String = "Mi",
    val totalUsos: Int = 0,
    val esOutfitDeHoy: Boolean = false
)
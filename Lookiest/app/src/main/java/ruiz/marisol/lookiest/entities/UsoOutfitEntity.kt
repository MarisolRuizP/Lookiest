package ruiz.marisol.lookiest.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usosOutfit")
data class UsoOutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val outfitId: Int = 0,
    val fecha: String
)
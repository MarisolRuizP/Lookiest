package ruiz.marisol.lookiest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usosOutfit")
data class UsoOutfit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String = "",
    val oufitId: Int,
    val fecha: String
)
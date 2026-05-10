package ruiz.marisol.lookiest.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ruiz.marisol.lookiest.data.Outfit

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits")
    fun obtenerTodosLosOutfits(): Flow<List<Outfit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOutfit(outfit: Outfit)

    @Update
    suspend fun actualizarOutfit(outfit: Outfit)

    @Delete
    suspend fun eliminarOutfit(outfit: Outfit)
}
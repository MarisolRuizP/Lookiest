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
    @Query("SELECT * FROM outfits ORDER BY id DESC")
    fun obtenerTodosLosOutfits(): Flow<List<Outfit>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun obtenerOutfitPorId(id: Int): Outfit?

    @Query("SELECT * FROM outfits WHERE esOutfitDeHoy = 1 LIMIT 1")
    fun obtenerOutfitDeHoy(): Flow<Outfit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOutfit(outfit: Outfit)

    @Update
    suspend fun actualizarOutfit(outfit: Outfit)

    @Delete
    suspend fun eliminarOutfit(outfit: Outfit)

    @Query("UPDATE outfits SET esOutfitDeHoy = 0")
    suspend fun resetOutfitDeHoy()

    @Query("UPDATE outfits SET esOutfitDeHoy = 1 WHERE id = :id")
    suspend fun setOutfitDeHoy(id: Int)

    @Query("UPDATE outfits SET totalUsos = totalUsos + 1 WHERE id = :id")
    suspend fun incrementarUsos(id: Int)
}
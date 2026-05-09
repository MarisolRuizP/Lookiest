package ruiz.marisol.lookiest.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ruiz.marisol.lookiest.data.local.entities.OutfitEntity

@Dao
interface OutfitDao {

    @Query("SELECT * FROM outfits ORDER BY id DESC")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getOutfitById(id: Int): OutfitEntity?

    @Query("SELECT * FROM outfits WHERE esOutfitDeHoy = 1 LIMIT 1")
    fun getOutfitDeHoy(): Flow<OutfitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity): Long

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Int)

    @Query("UPDATE outfits SET esOutfitDeHoy = 0")
    suspend fun resetOutfitDeHoy()

    @Query("UPDATE outfits SET esOutfitDeHoy = 1 WHERE id = :id")
    suspend fun setOutfitDeHoy(id: Int)
}
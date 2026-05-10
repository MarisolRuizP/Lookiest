package ruiz.marisol.lookiest.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ruiz.marisol.lookiest.entities.PrendaEntity


@Dao
interface PrendaDao {

    @Query("SELECT * FROM prendas ORDER BY nombre ASC")
    fun getAllPrendas(): Flow<List<PrendaEntity>>

    @Query("SELECT * FROM prendas WHERE id = :id")
    suspend fun getPrendaById(id: Int): PrendaEntity?

    @Query("SELECT * FROM prendas WHERE favorito = 1")
    fun getFavoritas(): Flow<List<PrendaEntity>>

    @Query("SELECT * FROM prendas WHERE usadaHoy = 1")
    fun getPrendasUsadasHoy(): Flow<List<PrendaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrenda(prenda: PrendaEntity): Long

    @Update
    suspend fun updatePrenda(prenda: PrendaEntity)

    @Query("DELETE FROM prendas WHERE id = :id")
    suspend fun deletePrendaById(id: Int)

    @Query("UPDATE prendas SET favorito = NOT favorito WHERE id = :id")
    suspend fun toggleFavorito(id: Int)

    @Query("UPDATE prendas SET usadaHoy = :usada WHERE id = :id")
    suspend fun setUsadaHoy(id: Int, usada: Boolean)

    @Query("UPDATE prendas SET usadaHoy = 0")
    suspend fun resetUsadasHoy()
}
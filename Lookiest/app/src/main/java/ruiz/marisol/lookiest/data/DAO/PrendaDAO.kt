package ruiz.marisol.lookiest.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ruiz.marisol.lookiest.data.PrendaRopa

@Dao
interface PrendaDao {
    @Query("SELECT * FROM prendas WHERE userEmail = :email ORDER BY nombre ASC")
    fun obtenerTodasLasPrendas(email: String): Flow<List<PrendaRopa>>

    @Query("SELECT * FROM prendas WHERE userEmail = :email AND favorito = 1")
    fun obtenerFavoritas(email: String): Flow<List<PrendaRopa>>

    @Query("SELECT * FROM prendas WHERE userEmail = :email AND usadaHoy = 1")
    fun obtenerUsadasHoy(email: String): Flow<List<PrendaRopa>>

    @Query("SELECT * FROM prendas WHERE id = :id")
    suspend fun obtenerPrendaPorId(id: Int): PrendaRopa?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrenda(prenda: PrendaRopa)

    @Update
    suspend fun actualizarPrenda(prenda: PrendaRopa)

    @Delete
    suspend fun eliminarPrenda(prenda: PrendaRopa)

    @Query("UPDATE prendas SET favorito = CASE WHEN favorito = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorito(id: Int)

    @Query("UPDATE prendas SET usadaHoy = :usada WHERE id = :id")
    suspend fun setUsadaHoy(id: Int, usada: Boolean)

    @Query("UPDATE prendas SET usadaHoy = 0")
    suspend fun resetUsadasHoy()
}
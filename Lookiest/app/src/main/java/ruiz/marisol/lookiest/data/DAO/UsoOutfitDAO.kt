package ruiz.marisol.lookiest.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ruiz.marisol.lookiest.data.UsoOutfit

@Dao
interface UsoOutfitDao {
    @Query("SELECT * FROM usosOutfit WHERE userEmail = :email ORDER BY fecha DESC")
    fun obtenerTodos(email: String): Flow<List<UsoOutfit>>

    @Query("SELECT * FROM usosOutfit WHERE userEmail = :email AND fecha = :fecha")
    suspend fun obtenerPorFecha(email: String, fecha: String): List<UsoOutfit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(uso: UsoOutfit)

    @Delete
    suspend fun eliminar(uso: UsoOutfit)

    @Query("DELETE FROM usosOutfit WHERE userEmail = :email AND fecha = :fecha")
    suspend fun eliminarPorFecha(email: String, fecha: String)
}
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(uso: UsoOutfit)

    @Query("SELECT * FROM usosOutfit")
    fun obtenerTodos(): Flow<List<UsoOutfit>>

    @Query("SELECT * FROM usosOutfit WHERE fecha = :fecha")
    fun obtenerPorFecha(fecha: String): Flow<List<UsoOutfit>>

    @Delete
    suspend fun eliminar(uso: UsoOutfit)
}
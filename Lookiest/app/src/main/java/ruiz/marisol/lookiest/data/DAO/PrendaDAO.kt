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
        @Query("SELECT * FROM prendas")
        fun obtenerTodasLasPrendas(): Flow<List<PrendaRopa>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertarPrenda(prenda: PrendaRopa)

        @Update
        suspend fun actualizarPrenda(prenda: PrendaRopa)

        @Delete
        suspend fun eliminarPrenda(prenda: PrendaRopa)
    }

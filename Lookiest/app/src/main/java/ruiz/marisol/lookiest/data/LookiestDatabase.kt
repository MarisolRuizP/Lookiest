package ruiz.marisol.lookiest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ruiz.marisol.lookiest.data.local.dao.OutfitDao
import ruiz.marisol.lookiest.data.local.dao.PrendaDao
import ruiz.marisol.lookiest.data.local.entities.OutfitEntity
import ruiz.marisol.lookiest.entities.PrendaEntity

@Database(
    entities = [PrendaEntity::class, OutfitEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LookiestDatabase : RoomDatabase() {

    abstract fun prendaDao(): PrendaDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile
        private var INSTANCE: LookiestDatabase? = null

        fun getDatabase(context: Context): LookiestDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LookiestDatabase::class.java,
                    "lookiest_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
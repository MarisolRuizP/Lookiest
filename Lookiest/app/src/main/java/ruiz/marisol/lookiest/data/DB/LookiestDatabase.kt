package ruiz.marisol.lookiest.data.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ruiz.marisol.lookiest.data.Converters
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa

@Database(entities = [PrendaRopa::class, Outfit::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LookiestDatabase : RoomDatabase() {

    abstract fun prendaDao(): PrendaDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile
        private var INSTANCE: LookiestDatabase? = null

        fun getDatabase(context: Context): LookiestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LookiestDatabase::class.java,
                    "lookiest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
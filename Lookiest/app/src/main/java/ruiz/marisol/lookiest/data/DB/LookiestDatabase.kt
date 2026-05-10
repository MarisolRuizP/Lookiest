package ruiz.marisol.lookiest.data.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ruiz.marisol.lookiest.data.Converters
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
@Database(
    entities = [PrendaRopa::class, Outfit::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LookiestDatabase : RoomDatabase() {

    abstract fun prendaDao(): PrendaDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile
        private var INSTANCE: LookiestDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE prendas ADD COLUMN usadaHoy INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE outfits ADD COLUMN totalUsos INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE outfits ADD COLUMN esOutfitDeHoy INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): LookiestDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LookiestDatabase::class.java,
                    "lookiest_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
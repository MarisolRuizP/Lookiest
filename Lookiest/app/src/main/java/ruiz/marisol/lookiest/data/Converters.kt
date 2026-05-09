package ruiz.marisol.lookiest.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // para listas de Strings (tags, temporadas)
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    // Para la lista de prendas dentro del Outfit
    @TypeConverter
    fun fromPrendaList(value: List<PrendaRopa>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPrendaList(value: String): List<PrendaRopa> {
        val listType = object : TypeToken<List<PrendaRopa>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
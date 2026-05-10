package ruiz.marisol.lookiest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    val email: String,
    val username: String,
    val nombre: String,
    val contrasena: String,
    val genero: String = "",
    val fechaNacimiento: String = "",
    val biometriaActiva: Boolean = false,
    val fotoPerfil: String? = null,
    val isDarkMode: Boolean = false
)

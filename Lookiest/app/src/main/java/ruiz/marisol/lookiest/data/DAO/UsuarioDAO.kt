package ruiz.marisol.lookiest.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ruiz.marisol.lookiest.data.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarUsuario(user: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): Usuario?

    @Query("UPDATE usuarios SET contrasena = :nuevaPass WHERE email = :email")
    suspend fun updatePassword(email: String, nuevaPass: String)

    @Query("UPDATE usuarios SET nombre = :nombre, username = :username WHERE email = :email")
    suspend fun updateUserProfile(email: String, nombre: String, username: String)

    @Query("UPDATE usuarios SET contrasena = :pass WHERE username = :username")
    suspend fun updatePasswordByUsername(username: String, pass: String)

    @Query("UPDATE usuarios SET fotoPerfil = :nuevaUri WHERE email = :email")
    suspend fun updateFotoPerfil(email: String, nuevaUri: String)

    @Query("SELECT * FROM usuarios WHERE email = :identificador OR username = :identificador LIMIT 1")
    suspend fun getUserByIdentifier(identificador: String): Usuario?
}


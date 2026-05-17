package ruiz.marisol.lookiest.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Usuario actual
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    val username: StateFlow<String>
        get() = MutableStateFlow(auth.currentUser?.email ?: "")

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Registro

    /**
     * Registra un nuevo usuario con correo y contraseña en Firebase.
     */
    fun registrar(
        email: String,
        password: String,
        displayName: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Todos los campos deben estar llenos"
            onError("Todos los campos deben estar llenos")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Crea el usuario en Firebase
                val resultado = auth.createUserWithEmailAndPassword(email, password).await()

                // Guarda el nombre de usuario en el perfil de Firebase
                if (displayName.isNotBlank()) {
                    val profileUpdates = userProfileChangeRequest {
                        this.displayName = displayName
                    }
                    resultado.user?.updateProfile(profileUpdates)?.await()
                }

                _currentUser.value = auth.currentUser
                onSuccess()
            } catch (e: Exception) {
                val msg = mensajeDeError(e)
                _errorMessage.value = msg
                onError(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Inicia sesión con correo y contraseña en Firebase.
     */
    fun login(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Ingresa tu correo y contraseña"
            onResult(false)
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = auth.currentUser
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value = "Usuario y/o contraseña incorrectos"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _errorMessage.value = null
    }


    fun updateProfile(nuevoNombre: String, nuevoCorreo: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profileUpdates = userProfileChangeRequest {
                    displayName = nuevoNombre
                }
                auth.currentUser?.updateProfile(profileUpdates)?.await()

                if (nuevoCorreo != auth.currentUser?.email) {
                    auth.currentUser?.verifyBeforeUpdateEmail(nuevoCorreo)?.await()
                }
                _currentUser.value = auth.currentUser
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar perfil: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(nuevaPass: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.currentUser?.updatePassword(nuevaPass)?.await()
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar contraseña: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun limpiarError() {
        _errorMessage.value = null
    }

    private fun mensajeDeError(e: Exception): String = when {
        e.message?.contains("already in use")    == true -> "Este correo ya está registrado"
        e.message?.contains("badly formatted")   == true -> "El formato del correo no es válido"
        e.message?.contains("at least 6")        == true -> "La contraseña debe tener al menos 6 caracteres"
        e.message?.contains("no user record")    == true -> "No existe una cuenta con ese correo"
        e.message?.contains("password is wrong") == true -> "Contraseña incorrecta"
        else -> "Error: ${e.message}"
    }

    // Compatibilidad con pantallas que leen esto
    val password: StateFlow<String> = MutableStateFlow("")
    val isDarkMode: StateFlow<Boolean> = MutableStateFlow(false)
    val biometriaHabilitada: StateFlow<Boolean> = MutableStateFlow(false)
}
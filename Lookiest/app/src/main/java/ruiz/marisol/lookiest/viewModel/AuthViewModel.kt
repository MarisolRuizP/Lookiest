package ruiz.marisol.lookiest.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.SharedPreferences

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var prefs: SharedPreferences? = null
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _username = MutableStateFlow(auth.currentUser?.email ?: "")
    val username: StateFlow<String> = _username.asStateFlow()
    private val _biometriaHabilitada = MutableStateFlow(false)
    val biometriaHabilitada: StateFlow<Boolean> = _biometriaHabilitada.asStateFlow()
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val password: StateFlow<String> = MutableStateFlow("")

    fun initPrefs(context: Context) {
        prefs = context.getSharedPreferences("lookiest_prefs", Context.MODE_PRIVATE)
        // Al inicializar carga el valor guardado del usuario actual
        val email = auth.currentUser?.email ?: ""
        if (email.isNotEmpty()) {
            _biometriaHabilitada.value = prefs!!.getBoolean("biometria_$email", false)
        }
    }

    fun verificarBiometria(email: String) {
        _biometriaHabilitada.value = prefs?.getBoolean("biometria_$email", false) ?: false
    }

    fun loginConBiometria(onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            _currentUser.value = user
            _isLoggedIn.value  = true
            _username.value    = user.email ?: ""
            onResult(true)
        } else {
            onResult(false)
        }
    }

    fun verificarSiExiste(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.fetchSignInMethodsForEmail(email).await()
                onResult(!result.signInMethods.isNullOrEmpty())
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun cargarDatosUsuario(email: String) {
        _username.value = email
    }

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
                val resultado = auth.createUserWithEmailAndPassword(email, password).await()
                if (displayName.isNotBlank()) {
                    val profileUpdates = userProfileChangeRequest { this.displayName = displayName }
                    resultado.user?.updateProfile(profileUpdates)?.await()
                }
                _currentUser.value = auth.currentUser
                _isLoggedIn.value  = true
                _username.value    = auth.currentUser?.email ?: ""
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
                _isLoggedIn.value  = true
                _username.value    = auth.currentUser?.email ?: ""
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
        _currentUser.value      = null
        _isLoggedIn.value       = false
        _username.value         = ""
        _biometriaHabilitada.value = false
        _errorMessage.value     = null
    }

    fun updateProfile(
        nuevoNombre: String,
        nuevoCorreo: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profileUpdates = userProfileChangeRequest { displayName = nuevoNombre }
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

    fun updatePassword(
        nuevaPass: String,
        passAnterior: String = "",
        onResult: (Boolean) -> Unit = {}
    ) {
        val user = auth.currentUser ?: return onResult(false)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (passAnterior.isNotBlank()) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, passAnterior)
                    user.reauthenticate(credential).await()
                }
                user.updatePassword(nuevaPass).await()
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar contraseña: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subirFoto(fileUri: Uri, context: Context, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return onResult(false)
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(fileUri)
                    .build()
                user.updateProfile(profileUpdates).await()
                auth.currentUser?.reload()?.await()
                _currentUser.value = auth.currentUser
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cambiarBiometriaFirebase(nuevoEstado: Boolean) {
        viewModelScope.launch {
            val email = auth.currentUser?.email ?: ""
            if (email.isNotEmpty()) {
                _biometriaHabilitada.value = nuevoEstado
                prefs?.edit()?.putBoolean("biometria_$email", nuevoEstado)?.apply()
            }
        }
    }

    fun cambiarTemaFirebase(estadoActual: Boolean) {
        viewModelScope.launch {
            if (auth.currentUser?.email?.isNotEmpty() == true) {
                _isDarkMode.value = !estadoActual
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
}
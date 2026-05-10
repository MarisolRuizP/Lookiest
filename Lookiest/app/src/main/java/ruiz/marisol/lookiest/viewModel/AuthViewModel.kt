package ruiz.marisol.lookiest.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ruiz.marisol.lookiest.data.DAO.UsuarioDao
import ruiz.marisol.lookiest.data.DataStoreManager
import ruiz.marisol.lookiest.data.Usuario

class AuthViewModel(
    private val userDao: UsuarioDao,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    private val _biometriaHabilitada = MutableStateFlow(false)
    private val _isDarkMode = MutableStateFlow(false)


    init {
        viewModelScope.launch {
            dataStore.usernameFlow.collect { name ->
                if (!name.isNullOrEmpty()) {
                    val user = userDao.getUserByUsername(name)
                    _usuarioLogueado.value = user
                    _biometriaHabilitada.value = user?.biometriaActiva ?: false
                }
            }
        }
    }
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado
    val isLoggedIn = dataStore.isLoggedInFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val username = dataStore.usernameFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )
    val password = dataStore.passwordFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )
    val biometriaHabilitada: StateFlow<Boolean> = _biometriaHabilitada
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun registrarEnRoom(entidad: Usuario) {
        viewModelScope.launch {
            userDao.registrarUsuario(entidad)
            dataStore.saveSession(entidad.username, entidad.contrasena)
        }
    }

    fun cargarDatosUsuario(userName: String) {
        viewModelScope.launch {
            _usuarioLogueado.value = userDao.getUserByUsername(userName)
        }
    }

    fun loginConRoom(identificador: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val usuario = userDao.getUserByIdentifier(identificador)

            if (usuario != null && usuario.contrasena == pass) {
                dataStore.saveSession(usuario.username, usuario.contrasena)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.logout()
        }
    }

    fun updateProfile(nuevoUsuario: String, nuevoNombre: String, nuevoCorreo: String) {
        viewModelScope.launch {
            userDao.updateUserProfile(nuevoCorreo, nuevoNombre, nuevoUsuario)
            dataStore.saveSession(nuevoUsuario, password.value)
        }
    }

    fun updatePassword(nuevaPass: String) {
        viewModelScope.launch {
            val currentUsername = usuarioLogueado.value?.username ?: username.value

            if (currentUsername.isNotEmpty()) {
                userDao.updatePasswordByUsername(currentUsername, nuevaPass)
                dataStore.saveSession(currentUsername, nuevaPass)
                val usuarioActualizado = userDao.getUserByUsername(currentUsername)
                _usuarioLogueado.value = usuarioActualizado
            }
        }
    }

    fun actualizarFotoPerfil(email: String, nuevaUri: String) {
        viewModelScope.launch {
            userDao.updateFotoPerfil(email, nuevaUri)
            cargarDatosUsuario(username.value)
        }
    }

    fun verificarBiometria(userName: String) {
        viewModelScope.launch {
            val usuario = userDao.getUserByUsername(userName)
            _biometriaHabilitada.value = usuario?.biometriaActiva ?: false
        }
    }

    fun actualizarBiometria(username: String, nuevoEstado: Boolean) {
        viewModelScope.launch {
            userDao.updateBiometria(username, nuevoEstado)
            val usuarioActualizado = userDao.getUserByUsername(username)
            _usuarioLogueado.value = usuarioActualizado
            _biometriaHabilitada.value = nuevoEstado
        }
    }

    fun loginConBiometria(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val identificador = username.value
            if (identificador.isNotEmpty()) {
                val usuario = userDao.getUserByUsername(identificador)
                if (usuario != null) {
                    dataStore.saveSession(usuario.username, usuario.contrasena)
                    _usuarioLogueado.value = usuario

                    onResult(true)
                } else {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun actualizarTheme(username: String, currentMode: Boolean) {
        viewModelScope.launch {
            val nuevoModo = !currentMode
            userDao.updateTheme(username, nuevoModo)
            _isDarkMode.value = nuevoModo
            cargarDatosUsuario(username)
        }
    }

    fun verificarSiExiste(username: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val usuario = userDao.getUserByUsername(username.trim())
            val existe = usuario != null
            onResult(existe)
        }
    }
}
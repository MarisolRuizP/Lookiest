package ruiz.marisol.lookiest.viewModel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ruiz.marisol.lookiest.data.NetworkSyncManager
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.OutfitRepository
import ruiz.marisol.lookiest.data.PrendaRepository
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.data.UsoOutfit
import ruiz.marisol.lookiest.data.UsoRepository
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri

class ClosetViewModel(
    private val prendaRepository: PrendaRepository,
    private val outfitRepository: OutfitRepository,
    private val usoRepository: UsoRepository,
    private val userEmail: String,
    context: Context
) : ViewModel() {

    val tallas       = listOf("XS", "S", "M", "L", "XL", "XXL")
    val categorias   = listOf("Top", "Bottom", "OuterWear", "BodySuit", "Zapatos", "Accesorios")
    val tags         = listOf("Leather", "Denim", "Pleated", "Knit", "Floral", "Lace")
    val temporadas   = listOf("Primavera", "Verano", "Otoño", "Invierno")
    val formalidades = listOf("Casual", "Formal", "Deportivo")
    val usuarioActualEmail: String get() = userEmail
    val colores      = listOf(
        "Rojo"     to Color(0xFF802626),
        "Azul"     to Color(0xFF0C6291),
        "Negro"    to Color(0xFF000004),
        "Amarillo" to Color(0xFFD8973C),
        "Rosa"     to Color(0xFFA73266),
        "Blanco"   to Color.White,
        "Verde"    to Color(0xFF2D6A4F),
        "Café"     to Color(0xFF6B4226),
        "Gris"     to Color(0xFF8E8E93),
        "Morado"   to Color(0xFF6A0572)
    )

    init {
        NetworkSyncManager(context, outfitRepository, prendaRepository, usoRepository)
            .startListening(viewModelScope)
        iniciarMonitorConexion(context)
    }

    private val _hayInternet = MutableStateFlow(true)
    val hayInternet: StateFlow<Boolean> = _hayInternet.asStateFlow()

    private fun iniciarMonitorConexion(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _hayInternet.value = true
            }
            override fun onLost(network: Network) {
                _hayInternet.value = false
            }
        })
    }

    val prendas: StateFlow<List<PrendaRopa>> = prendaRepository
        .obtenerTodasLasPrendas(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfits: StateFlow<List<Outfit>> = outfitRepository
        .obtenerOutfits(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prendasUsadasHoy: StateFlow<List<PrendaRopa>> = prendaRepository
        .obtenerUsadasHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfitDeHoy: StateFlow<Outfit?> = outfitRepository
        .obtenerOutfitDeHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val usos: StateFlow<List<UsoOutfit>> = usoRepository
        .obtenerTodos(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarPrenda(prenda: PrendaRopa, imagenUri: Uri? = null) = viewModelScope.launch {
        prendaRepository.guardarPrenda(prenda.copy(userEmail = userEmail), imagenUri)
    }

    fun eliminarPrenda(prenda: PrendaRopa) = viewModelScope.launch {
        prendaRepository.eliminarPrenda(prenda)
    }

    fun actualizarPrenda(prendaActualizada: PrendaRopa, imagenUri: Uri? = null) = viewModelScope.launch {
        prendaRepository.actualizarPrenda(prendaActualizada, imagenUri)
    }

    fun favorito(prenda: PrendaRopa) = viewModelScope.launch {
        prendaRepository.toggleFavorito(prenda.id)
    }

    fun toggleUsadaHoy(id: Int, usada: Boolean) = viewModelScope.launch {
        prendaRepository.setUsadaHoy(id, usada)
    }

    fun resetUsadasHoy() = viewModelScope.launch {
        prendaRepository.resetUsadasHoy()
    }

    fun agregarOutfit(outfit: Outfit) = viewModelScope.launch {
        outfitRepository.guardarOutfit(outfit.copy(userEmail = userEmail))
    }

    fun eliminarOutfit(outfit: Outfit) = viewModelScope.launch {
        outfitRepository.eliminarOutfit(outfit)
    }

    fun actualizarOutfit(outfitActualizado: Outfit) = viewModelScope.launch {
        outfitRepository.actualizarOutfit(outfitActualizado)
    }

    private val _outfitsPublicos = MutableStateFlow<List<Outfit>>(emptyList())
    val outfitsPublicos: StateFlow<List<Outfit>> = _outfitsPublicos.asStateFlow()

    fun cargarOutfitsPublicos() = viewModelScope.launch {
        _outfitsPublicos.value = outfitRepository.obtenerOutfitsPublicos()
    }

    fun setOutfitDeHoy(outfitId: Int) = viewModelScope.launch {
        outfitRepository.setOutfitDeHoy(userEmail, outfitId)
        outfitRepository.incrementarUsos(outfitId)
        usoRepository.insertar(
            UsoOutfit(
                userEmail = userEmail,
                oufitId   = outfitId,
                fecha     = java.time.LocalDate.now().toString()
            )
        )
    }

    fun eliminarUso(uso: UsoOutfit) = viewModelScope.launch {
        usoRepository.eliminar(uso)
    }

    fun guardarUsoDiario() = viewModelScope.launch {
        val hoy = java.time.LocalDate.now().toString()
        val ids = prendasUsadasHoy.value.map { it.id }
        if (ids.isEmpty()) return@launch
        usoRepository.eliminarPorFecha(userEmail, hoy)
        ids.forEach { prendaId ->
            usoRepository.insertar(UsoOutfit(userEmail = userEmail, oufitId = prendaId, fecha = hoy))
        }
    }

    fun prendasUsadasEn(fecha: String): List<PrendaRopa> {
        val prendaIds = usos.value
            .filter { it.fecha == fecha }
            .map { it.oufitId }
        return prendas.value.filter { it.id in prendaIds }
    }

    fun toggleLike(outfit: Outfit, liked: Boolean) = viewModelScope.launch {
        val nuevosLikes = if (liked) (outfit.likes ?: 0) + 1 else (outfit.likes ?: 0) - 1
        val actualizado = outfit.copy(likes = nuevosLikes)
        if (outfit.id != 0) {
            outfitRepository.actualizarOutfit(actualizado)
        } else if (outfit.firestoreId.isNotEmpty()) {
            outfitRepository.actualizarLikesFirestore(outfit.firestoreId, nuevosLikes, outfit.favoritos ?: 0)
        }
    }

    fun toggleFavoritoOutfit(outfit: Outfit, favorito: Boolean) = viewModelScope.launch {
        val nuevosFavs = if (favorito) (outfit.favoritos ?: 0) + 1 else (outfit.favoritos ?: 0) - 1
        val actualizado = outfit.copy(favoritos = nuevosFavs)
        if (outfit.id != 0) {
            outfitRepository.actualizarOutfit(actualizado)
        } else if (outfit.firestoreId.isNotEmpty()) {
            outfitRepository.actualizarLikesFirestore(outfit.firestoreId, outfit.likes ?: 0, nuevosFavs)
        }
    }
}
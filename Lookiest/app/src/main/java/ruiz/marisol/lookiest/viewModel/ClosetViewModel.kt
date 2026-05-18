package ruiz.marisol.lookiest.viewModel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao
import ruiz.marisol.lookiest.data.NetworkSyncManager
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.OutfitRepository
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.data.UsoOutfit
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClosetViewModel(
    private val prendaDAO: PrendaDao,
    private val outfitRepository: OutfitRepository,
    private val usoDAO: UsoOutfitDao,
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
        NetworkSyncManager(context, outfitRepository)
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

    val prendas: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerTodasLasPrendas(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfits: StateFlow<List<Outfit>> = outfitRepository
        .obtenerOutfits(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prendasUsadasHoy: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerUsadasHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfitDeHoy: StateFlow<Outfit?> = outfitRepository
        .obtenerOutfitDeHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val usos: StateFlow<List<UsoOutfit>> = usoDAO
        .obtenerTodos(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarPrenda(prenda: PrendaRopa) = viewModelScope.launch {
        prendaDAO.insertarPrenda(prenda.copy(userEmail = userEmail))
    }

    fun eliminarPrenda(prenda: PrendaRopa) = viewModelScope.launch {
        prendaDAO.eliminarPrenda(prenda)
    }

    fun actualizarPrenda(prendaActualizada: PrendaRopa) = viewModelScope.launch {
        prendaDAO.actualizarPrenda(prendaActualizada)
    }

    fun favorito(prenda: PrendaRopa) = viewModelScope.launch {
        prendaDAO.actualizarPrenda(prenda.copy(favorito = !prenda.favorito))
    }

    fun toggleUsadaHoy(id: Int, usada: Boolean) = viewModelScope.launch {
        prendaDAO.setUsadaHoy(id, usada)
    }

    fun resetUsadasHoy() = viewModelScope.launch {
        prendaDAO.resetUsadasHoy()
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

    fun setOutfitDeHoy(outfitId: Int) = viewModelScope.launch {
        outfitRepository.setOutfitDeHoy(userEmail, outfitId)
        outfitRepository.incrementarUsos(outfitId)
        usoDAO.insertar(
            UsoOutfit(
                userEmail = userEmail,
                oufitId   = outfitId,
                fecha     = java.time.LocalDate.now().toString()
            )
        )
    }

    fun eliminarUso(uso: UsoOutfit) = viewModelScope.launch {
        usoDAO.eliminar(uso)
    }

    fun guardarUsoDiario() = viewModelScope.launch {
        val hoy = java.time.LocalDate.now().toString()
        val ids = prendasUsadasHoy.value.map { it.id }
        if (ids.isEmpty()) return@launch
        usoDAO.eliminarPorFecha(userEmail, hoy)
        ids.forEach { prendaId ->
            usoDAO.insertar(UsoOutfit(userEmail = userEmail, oufitId = prendaId, fecha = hoy))
        }
    }

    fun prendasUsadasEn(fecha: String): List<PrendaRopa> {
        val prendaIds = usos.value
            .filter { it.fecha == fecha }
            .map { it.oufitId }
        return prendas.value.filter { it.id in prendaIds }
    }
}
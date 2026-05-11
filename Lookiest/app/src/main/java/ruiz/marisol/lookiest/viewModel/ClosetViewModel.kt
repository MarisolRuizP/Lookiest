package ruiz.marisol.lookiest.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.data.UsoOutfit
import androidx.compose.ui.graphics.Color

class ClosetViewModel(
    private val prendaDAO: PrendaDao,
    private val outfitDAO: OutfitDao,
    private val usoDAO: UsoOutfitDao,
    private val userEmail: String
) : ViewModel() {

    private val _tallas      = listOf("XS", "S", "M", "L", "XL", "XXL")
    private val _categorias  = listOf("Top", "Bottom", "OuterWear", "BodySuit", "Zapatos", "Accesorios")
    private val _tags        = listOf("Leather", "Denim", "Pleated", "Knit", "Floral", "Lace")
    private val _temporadas  = listOf("Primavera", "Verano", "Otoño", "Invierno")
    private val _formalidades = listOf("Casual", "Formal", "Deportivo")
    private val _opcionesColores = listOf(
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

    val tallas       get() = _tallas
    val categorias   get() = _categorias
    val tags         get() = _tags
    val temporadas   get() = _temporadas
    val formalidades get() = _formalidades
    val colores      get() = _opcionesColores

    val prendas: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerTodasLasPrendas(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfits: StateFlow<List<Outfit>> = outfitDAO
        .obtenerTodosLosOutfits(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prendasUsadasHoy: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerUsadasHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfitDeHoy: StateFlow<Outfit?> = outfitDAO
        .obtenerOutfitDeHoy(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val usos: StateFlow<List<UsoOutfit>> = usoDAO
        .obtenerTodos(userEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarUso(uso: UsoOutfit) = viewModelScope.launch {
        usoDAO.eliminar(uso)
    }

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
        outfitDAO.insertarOutfit(outfit.copy(userEmail = userEmail))
    }

    fun eliminarOutfit(outfit: Outfit) = viewModelScope.launch {
        outfitDAO.eliminarOutfit(outfit)
    }

    fun actualizarOutfit(outfitActualizado: Outfit) = viewModelScope.launch {
        outfitDAO.actualizarOutfit(outfitActualizado)
    }

    fun setOutfitDeHoy(outfitId: Int) = viewModelScope.launch {
        outfitDAO.resetOutfitDeHoy(userEmail)
        outfitDAO.setOutfitDeHoy(outfitId)
        outfitDAO.incrementarUsos(outfitId)
        usoDAO.insertar(
            UsoOutfit(
                userEmail = userEmail,
                oufitId   = outfitId,
                fecha     = java.time.LocalDate.now().toString()
            )
        )
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
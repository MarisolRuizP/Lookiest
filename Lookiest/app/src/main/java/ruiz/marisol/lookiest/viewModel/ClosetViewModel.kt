package ruiz.marisol.lookiest.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
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
class ClosetViewModel(
    private val prendaDAO: PrendaDao,
    private val outfitDAO: OutfitDao,
    private val usoDAO: UsoOutfitDao
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

    // ── Flows de Room ──────────────────────────────────────────────────────
    val prendas: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerTodasLasPrendas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfits: StateFlow<List<Outfit>> = outfitDAO
        .obtenerTodosLosOutfits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow de prendas marcadas como "usadas hoy" (necesita el DAO actualizado)
    val prendasUsadasHoy: StateFlow<List<PrendaRopa>> = prendaDAO
        .obtenerUsadasHoy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow del outfit marcado como "outfit de hoy"
    val outfitDeHoy: StateFlow<Outfit?> = outfitDAO
        .obtenerOutfitDeHoy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    val usos: StateFlow<List<UsoOutfit>> = usoDAO
        .obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registrarUso(outfitId: Int, fecha: String) = viewModelScope.launch {
        usoDAO.insertar(UsoOutfit(oufitId = outfitId, fecha = fecha))
    }

    fun eliminarUso(uso: UsoOutfit) = viewModelScope.launch {
        usoDAO.eliminar(uso)
    }

    fun prendasUsadasEn(fecha: String): List<PrendaRopa> {
        val outfitIds = usos.value
            .filter { it.fecha == fecha }
            .map { it.oufitId }

        val prendaIds = outfits.value
            .filter { it.id in outfitIds }
            .flatMap { outfit ->
                outfit.prendas.split(",").mapNotNull { it.trim().toIntOrNull() }
            }
            .distinct()
        return prendas.value.filter { it.id in prendaIds }
    }


    fun agregarPrenda(prenda: PrendaRopa) = viewModelScope.launch {
        prendaDAO.insertarPrenda(prenda)
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

    /** Marca o desmarca una prenda como usada hoy */
    fun toggleUsadaHoy(id: Int, usada: Boolean) = viewModelScope.launch {
        prendaDAO.setUsadaHoy(id, usada)
    }

    /** Desmarca todas las prendas como "usadas hoy" (para resetear al día siguiente) */
    fun resetUsadasHoy() = viewModelScope.launch {
        prendaDAO.resetUsadasHoy()
    }

    /**
     * Guarda el uso diario actual en el calendario.
     * Llama esto cuando el usuario confirma "Guardar Outfit" en OutfitDeHoyScreen.
     */
    fun guardarUsoDiario() {
        val hoy = java.time.LocalDate.now().toString()
        // Registramos un uso por outfit de hoy si existe
        val hoyOutfit = outfitDeHoy.value
        if (hoyOutfit != null) {
            registrarUso(hoyOutfit.id, hoy)
        }
    }

    // ── Outfits ────────────────────────────────────────────────────────────

    /** Inserta un Outfit directamente (usado en CrearOutfitScreen) */
    fun agregarOutfit(outfit: Outfit) = viewModelScope.launch {
        outfitDAO.insertarOutfit(outfit)
    }

    fun eliminarOutfit(outfit: Outfit) = viewModelScope.launch {
        outfitDAO.eliminarOutfit(outfit)
    }

    fun actualizarOutfit(outfitActualizado: Outfit) = viewModelScope.launch {
        outfitDAO.actualizarOutfit(outfitActualizado)
    }

    /**
     * Marca un outfit como "el outfit de hoy" e incrementa sus usos.
     * También registra el uso en el calendario.
     */
    fun setOutfitDeHoy(outfitId: Int) = viewModelScope.launch {
        outfitDAO.resetOutfitDeHoy()
        outfitDAO.setOutfitDeHoy(outfitId)
        outfitDAO.incrementarUsos(outfitId)
        registrarUso(outfitId, java.time.LocalDate.now().toString())
    }
}
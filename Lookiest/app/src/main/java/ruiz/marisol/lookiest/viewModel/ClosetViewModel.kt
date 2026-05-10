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
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.data.UsoOutfit

class ClosetViewModel(
    private val prendaDAO : PrendaDao,
    private val outfitDAO : OutfitDao
) : ViewModel() {

    private val _tallas = listOf("XS", "S", "M", "L", "XL", "XXL")
    private val _categorias = listOf("Top", "Bottom", "OuterWear", "BodySuit", "Zapatos", "Accesorios")
    private val _tags = listOf("Leather", "Denim", "Pleated", "Knit", "Floral", "Lace")
    private val _temporadas = listOf("Primavera", "Verano", "Otoño", "Invierno")
    private val _formalidades = listOf("Casual", "Formal", "Deportivo")

    private val _opcionesColores = listOf("Rojo" to Color.Red, "Azul" to Color.Blue, "Verde" to Color.Green, "Negro" to Color.Black)

    // Getters públicos
    val tallas get() = _tallas
    val categorias get() = _categorias
    val tags get() = _tags
    val temporadas get() = _temporadas
    val formalidades get() = _formalidades
    val colores get() = _opcionesColores

    val prendas: StateFlow<List<PrendaRopa>> = prendaDAO.obtenerTodasLasPrendas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val outfits: StateFlow<List<Outfit>> = outfitDAO.obtenerTodosLosOutfits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarPrenda(prenda: PrendaRopa) {
        viewModelScope.launch {
            prendaDAO.insertarPrenda(prenda)
        }
    }

    fun eliminarPrenda(prenda: PrendaRopa) {
        viewModelScope.launch {
            prendaDAO.eliminarPrenda(prenda)
        }
    }

    fun actualizarPrenda(prendaActualizada: PrendaRopa) {
        viewModelScope.launch {
            prendaDAO.actualizarPrenda(prendaActualizada)
        }
    }


    fun favorito(prenda: PrendaRopa) {
        viewModelScope.launch {
            val prendaModificada = prenda.copy(favorito = !prenda.favorito)
            prendaDAO.actualizarPrenda(prendaModificada)
        }
    }

    var usos by mutableStateOf(listOf<UsoOutfit>())
        private set

    fun registrarUso(outfitId: Int, fecha: String) {
        usos = usos + UsoOutfit(
            id = usos.size +1,
            oufitId = outfitId,
            fecha = fecha
        )
    }

    fun eliminarUso(id: Int) {
        usos = usos.filter { it.id != id }
    }

    fun prendasUsadasEn(fecha: String): List<PrendaRopa> {
        val outfitIds = usos.filter { it.fecha == fecha }.map { it.oufitId }
        return outfits.value
            .filter { it.id in outfitIds }
            .flatMap { outfit ->
                outfit.prendas.split(",").mapNotNull { it.trim().toIntOrNull() }
            }
            .distinct()
            .let { outfitIds ->
                prendas.value.filter {it.id in outfitIds}
            }
    }

    fun agregarOutfit(outfit: Outfit) {
        viewModelScope.launch {
            outfitDAO.insertarOutfit(outfit)
        }
    }

    fun eliminarOutfit(outfit: Outfit) {
        viewModelScope.launch {
            outfitDAO.eliminarOutfit(outfit)
        }
    }

    fun actualizarOutfit(outfitActualizado: Outfit) {
        viewModelScope.launch {
            outfitDAO.actualizarOutfit(outfitActualizado)
        }
    }
}
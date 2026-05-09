package ruiz.marisol.lookiest.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa

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

    val prendas: Flow<List<PrendaRopa>> = prendaDAO.obtenerTodasLasPrendas()
    val outfits: Flow<List<Outfit>> = outfitDAO.obtenerTodosLosOutfits()

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
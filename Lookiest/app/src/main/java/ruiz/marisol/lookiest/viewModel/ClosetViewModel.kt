package ruiz.marisol.lookiest.viewModel

import androidx.lifecycle.ViewModel
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.PrendaRopa

class ClosetViewModel : ViewModel() {

    private val items = mutableListOf(
        PrendaRopa(
            id = 1,
            nombre = "Chaqueta roja de vinipiel",
            tienda = "Zara",
            talla = "M",
            color = "Rojo",
            estampado = false,
            categoria = "OuterWear",
            tags = listOf("Leather"),
            temporada = listOf("Otoño", "Invierno"),
            formalidad = "Casual",
            imagen = R.drawable.chaqueta_roja
        ),
        PrendaRopa(
            id = 2,
            nombre = "Falda roja con patoles",
            tienda = "",
            talla = "XS",
            color = "Rojo",
            estampado = true,
            categoria = "Bottom",
            tags = listOf("Pleated"),
            temporada = listOf("Primavera", "Verano"),
            formalidad = "Casual",
            imagen = R.drawable.falda_roja
        )
    )

    val prendas: List<PrendaRopa> get() = items

    fun favorito(id: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items[index] = items[index].copy(favorito = !items[index].favorito)
        }
    }

    fun agregarPrenda(prenda: PrendaRopa) {
        items.add(prenda)
    }

    fun eliminarPrenda(id: Int) {
        items.removeIf { it.id == id }
    }

}
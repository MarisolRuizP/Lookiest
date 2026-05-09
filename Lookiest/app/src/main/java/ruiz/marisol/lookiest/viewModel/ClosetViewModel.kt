package ruiz.marisol.lookiest.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa

class ClosetViewModel : ViewModel() {

    private val _tallas = listOf("XS", "S", "M", "L", "XL", "XXL")
    private val _categorias = listOf("Top", "Bottom", "OuterWear", "BodySuit", "Zapatos", "Accesorios")
    private val _tags = listOf("Leather", "Denim", "Pleated", "Knit", "Floral", "Lace")
    private val _temporadas = listOf("Primavera", "Verano", "Otoño", "Invierno")
    private val _formalidades = listOf("Casual", "Formal", "Deportivo")

    private val _opcionesColores = listOf("Rojo" to Color.Red, "Azul" to Color.Blue, "Verde" to Color.Green, "Negro" to Color.Black)

    // Getters públicos (los que usará tu UI)
    val tallas get() = _tallas
    val categorias get() = _categorias
    val tags get() = _tags
    val temporadas get() = _temporadas
    val formalidades get() = _formalidades
    val colores get() = _opcionesColores

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

    var outfits by mutableStateOf(listOf<Outfit>()) // lista de outfits
        private set

    fun agregarOutfit(
        nombre: String,
        prendasIds: Set<Int>,
        esPublico: Boolean,
        etiquetas: List<String>
    ) {
        val prendasDelOutfit = items.filter { it.id in prendasIds }
        outfits = outfits + Outfit(
            id        = outfits.size + 1,
            nombre    = nombre,
            prendas   = prendasDelOutfit,
            esPublico = esPublico,
            etiquetas = etiquetas
        )
    }


    fun eliminarOutfit(id: Int) {
        outfits = outfits.filter { it.id != id }
    }

    fun actualizarOutfit(
        outfitId: Int,
        nombre: String,
        prendasIds: Set<Int>,
        esPublico: Boolean,
        etiquetas: List<String>
    ) {
        val prendasDelOutfit = items.filter { it.id in prendasIds }
        outfits = outfits.map { o ->
            if (o.id == outfitId)
                o.copy(
                    nombre    = nombre,
                    prendas   = prendasDelOutfit,
                    esPublico = esPublico,
                    etiquetas = etiquetas
                )
            else o
        }
    }
    fun getOutfitById(id: Int): Outfit? = outfits.find { it.id == id }
}

}
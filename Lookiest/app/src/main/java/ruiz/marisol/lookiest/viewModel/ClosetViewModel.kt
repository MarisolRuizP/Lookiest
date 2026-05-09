package ruiz.marisol.lookiest.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import ruiz.marisol.lookiest.R
import ruiz.marisol.lookiest.data.Outfit
import ruiz.marisol.lookiest.data.PrendaRopa
import ruiz.marisol.lookiest.data.UsoOutfit

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

    var prendas by mutableStateOf(listOf(
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
    ))
        private set


    fun favorito(id: Int) {
        prendas = prendas.map { prenda ->
            if (prenda.id == id) prenda.copy(favorito = !prenda.favorito)
            else prenda
        }
    }

    fun agregarPrenda(prenda: PrendaRopa) {
        prendas = prendas + prenda
    }

    fun eliminarPrenda(id: Int) {
        prendas = prendas.filter { it.id != id }
    }

    var outfits by mutableStateOf(listOf<Outfit>()) // lista de outfits
        private set

    fun agregarOutfit(
        nombre: String,
        prendasIds: Set<Int>,
        esPublico: Boolean,
        etiquetas: List<String>
    ) {
        val prendasDelOutfit = prendas.filter { it.id in prendasIds }
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
        val prendasDelOutfit = prendas.filter { it.id in prendasIds }
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
        return outfits
            .filter { it.id in outfitIds }
            .flatMap { it.prendas }
            .distinctBy { it.id }
    }
}
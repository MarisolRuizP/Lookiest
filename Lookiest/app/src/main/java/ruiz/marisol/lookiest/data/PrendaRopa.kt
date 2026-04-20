package ruiz.marisol.lookiest.data

data class PrendaRopa (
    val id: Int,
    val nombre: String,
    val tienda: String = "",
    val talla: String,
    val color: String,
    val estampado: Boolean,
    val categoria: String,
    val tags: List<String> = emptyList(),
    val temporada: List<String> = emptyList(),
    val formalidad: String,
    val imagen: Int? = null,
    val favorito: Boolean = false
)
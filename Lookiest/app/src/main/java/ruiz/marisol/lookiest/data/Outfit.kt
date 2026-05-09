package ruiz.marisol.lookiest.data

data class Outfit(
    val id: Int,
    val nombre: String = "",
    val prendas: List<PrendaRopa> = emptyList(),
    val esPublico: Boolean = false,
    val etiquetas: List<String> = emptyList(),
    val creadoPor: String = "Mi",
    val likes : Int? = 0,
    val favoritos : Int? = 0
)
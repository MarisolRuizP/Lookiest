package ruiz.marisol.lookiest.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import ruiz.marisol.lookiest.data.DAO.PrendaDao

class PrendaRepository(
    private val prendaDao: PrendaDao,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val prendasRef = firestore.collection("prendas")

    fun obtenerTodasLasPrendas(email: String): Flow<List<PrendaRopa>> =
        prendaDao.obtenerTodasLasPrendas(email)

    fun obtenerFavoritas(email: String): Flow<List<PrendaRopa>> =
        prendaDao.obtenerFavoritas(email)

    fun obtenerUsadasHoy(email: String): Flow<List<PrendaRopa>> =
        prendaDao.obtenerUsadasHoy(email)

    suspend fun obtenerPrendaPorId(id: Int): PrendaRopa? =
        prendaDao.obtenerPrendaPorId(id)

    suspend fun guardarPrenda(prenda: PrendaRopa) {
        val id = prendaDao.insertarPrenda(prenda)
        if (hayInternet()) {
            subirAFirestore(prenda.copy(id = id.toInt()))
        }
    }

    suspend fun actualizarPrenda(prenda: PrendaRopa) {
        prendaDao.actualizarPrenda(prenda)
        if (hayInternet() && prenda.firestoreId.isNotEmpty()) {
            actualizarEnFirestore(prenda)
        }
    }

    suspend fun eliminarPrenda(prenda: PrendaRopa) {
        prendaDao.eliminarPrenda(prenda)
        if (hayInternet() && prenda.firestoreId.isNotEmpty()) {
            prendasRef.document(prenda.firestoreId).delete().await()
        }
    }

    suspend fun toggleFavorito(id: Int) {
        prendaDao.toggleFavorito(id)
        val prenda = prendaDao.obtenerPrendaPorId(id)
        if (prenda != null && hayInternet() && prenda.firestoreId.isNotEmpty()) {
            actualizarEnFirestore(prenda)
        }
    }

    suspend fun setUsadaHoy(id: Int, usada: Boolean) {
        prendaDao.setUsadaHoy(id, usada)
        val prenda = prendaDao.obtenerPrendaPorId(id)
        if (prenda != null && hayInternet() && prenda.firestoreId.isNotEmpty()) {
            actualizarEnFirestore(prenda)
        }
    }

    suspend fun resetUsadasHoy() {
        prendaDao.resetUsadasHoy()
    }

    suspend fun sincronizarPendientes() {
        if (!hayInternet()) return
        val pendientes = prendaDao.obtenerPendientesDeSync()
        pendientes.forEach { subirAFirestore(it) }
    }

    private suspend fun subirAFirestore(prenda: PrendaRopa) {
        try {
            val data = mapOf(
                "userEmail" to prenda.userEmail,
                "nombre" to prenda.nombre,
                "tienda" to prenda.tienda,
                "talla" to prenda.talla,
                "color" to prenda.color,
                "estampado" to prenda.estampado,
                "categoria" to prenda.categoria,
                "tags" to prenda.tags,
                "temporada" to prenda.temporada,
                "formalidad" to prenda.formalidad,
                "imagen" to prenda.imagen,
                "favorito" to prenda.favorito,
                "usadaHoy" to prenda.usadaHoy
            )
            val docRef = prendasRef.add(data).await()
            prendaDao.marcarComoSincronizado(prenda.id, docRef.id)
        } catch (e: Exception) {
        }
    }

    private suspend fun actualizarEnFirestore(prenda: PrendaRopa) {
        try {
            val data = mapOf(
                "nombre" to prenda.nombre,
                "tienda" to prenda.tienda,
                "talla" to prenda.talla,
                "color" to prenda.color,
                "estampado" to prenda.estampado,
                "categoria" to prenda.categoria,
                "tags" to prenda.tags,
                "temporada" to prenda.temporada,
                "formalidad" to prenda.formalidad,
                "imagen" to prenda.imagen,
                "favorito" to prenda.favorito,
                "usadaHoy" to prenda.usadaHoy
            )
            prendasRef.document(prenda.firestoreId).update(data).await()
        } catch (e: Exception) { }
    }

    private fun hayInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

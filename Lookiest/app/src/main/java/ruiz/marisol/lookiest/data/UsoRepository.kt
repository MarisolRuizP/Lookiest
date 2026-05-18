package ruiz.marisol.lookiest.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao

class UsoRepository(
    private val usoDao: UsoOutfitDao,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val usosRef = firestore.collection("usos")

    fun obtenerTodos(email: String): Flow<List<UsoOutfit>> =
        usoDao.obtenerTodos(email)

    suspend fun obtenerPorFecha(email: String, fecha: String): List<UsoOutfit> =
        usoDao.obtenerPorFecha(email, fecha)

    suspend fun insertar(uso: UsoOutfit) {
        usoDao.insertar(uso)
        if (hayInternet()) subirAFirestore(uso)
    }

    suspend fun eliminar(uso: UsoOutfit) {
        usoDao.eliminar(uso)
        if (hayInternet() && uso.firestoreId.isNotEmpty()) {
            usosRef.document(uso.firestoreId).delete().await()
        }
    }

    suspend fun eliminarPorFecha(email: String, fecha: String) {
        val usos = usoDao.obtenerPorFecha(email, fecha)
        usoDao.eliminarPorFecha(email, fecha)
        if (hayInternet()) {
            usos.forEach { uso ->
                if (uso.firestoreId.isNotEmpty()) {
                    usosRef.document(uso.firestoreId).delete().await()
                }
            }
        }
    }

    suspend fun sincronizarPendientes() {
        if (!hayInternet()) return
        val pendientes = usoDao.obtenerPendientesDeSync()
        pendientes.forEach { subirAFirestore(it) }
    }

    private suspend fun subirAFirestore(uso: UsoOutfit) {
        try {
            val data = mapOf(
                "userEmail" to uso.userEmail,
                "oufitId" to uso.oufitId,
                "fecha" to uso.fecha
            )
            val docRef = usosRef.add(data).await()
            usoDao.marcarComoSincronizado(uso.id, docRef.id)
        } catch (e: Exception) {
        }
    }

    private fun hayInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

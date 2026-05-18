package ruiz.marisol.lookiest.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import ruiz.marisol.lookiest.data.DAO.OutfitDao

class OutfitRepository(
    private val outfitDao: OutfitDao,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val outfitsRef = firestore.collection("outfits")

    fun obtenerOutfits(email: String): Flow<List<Outfit>> =
        outfitDao.obtenerTodosLosOutfits(email)

    fun obtenerOutfitDeHoy(email: String): Flow<Outfit?> =
        outfitDao.obtenerOutfitDeHoy(email)

    suspend fun guardarOutfit(outfit: Outfit) {
        outfitDao.insertarOutfit(outfit)
        if (hayInternet()) subirAFirestore(outfit)
    }

    suspend fun actualizarOutfit(outfit: Outfit) {
        outfitDao.actualizarOutfit(outfit)
        if (hayInternet() && outfit.firestoreId.isNotEmpty()) {
            actualizarEnFirestore(outfit)
        }
    }

    suspend fun eliminarOutfit(outfit: Outfit) {
        outfitDao.eliminarOutfit(outfit)
        if (hayInternet() && outfit.firestoreId.isNotEmpty()) {
            outfitsRef.document(outfit.firestoreId).delete().await()
        }
    }

    suspend fun setOutfitDeHoy(email: String, id: Int) {
        outfitDao.resetOutfitDeHoy(email)
        outfitDao.setOutfitDeHoy(id)
    }

    suspend fun incrementarUsos(id: Int) {
        outfitDao.incrementarUsos(id)
    }

    suspend fun sincronizarPendientes() {
        if (!hayInternet()) return
        val pendientes = outfitDao.obtenerPendientesDeSync()
        pendientes.forEach { subirAFirestore(it) }
    }

    private suspend fun subirAFirestore(outfit: Outfit) {
        try {
            val data = mapOf(
                "userEmail"     to outfit.userEmail,
                "nombre"        to outfit.nombre,
                "prendas"       to outfit.prendas,
                "esPublico"     to outfit.esPublico,
                "etiquetas"     to outfit.etiquetas,
                "creadoPor"     to outfit.creadoPor,
                "likes"         to (outfit.likes ?: 0),
                "favoritos"     to (outfit.favoritos ?: 0),
                "totalUsos"     to outfit.totalUsos,
                "esOutfitDeHoy" to outfit.esOutfitDeHoy
            )
            val docRef = outfitsRef.add(data).await()
            outfitDao.marcarComoSincronizado(outfit.id, docRef.id)
        } catch (e: Exception) {
            // queda syncPendiente = true, se reintenta después
        }
    }

    private suspend fun actualizarEnFirestore(outfit: Outfit) {
        try {
            val data = mapOf(
                "nombre"        to outfit.nombre,
                "prendas"       to outfit.prendas,
                "esPublico"     to outfit.esPublico,
                "etiquetas"     to outfit.etiquetas,
                "totalUsos"     to outfit.totalUsos,
                "esOutfitDeHoy" to outfit.esOutfitDeHoy
            )
            outfitsRef.document(outfit.firestoreId).update(data).await()
        } catch (e: Exception) { }
    }

    private fun hayInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
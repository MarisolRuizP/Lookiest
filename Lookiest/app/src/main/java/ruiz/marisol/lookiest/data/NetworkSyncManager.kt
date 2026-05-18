package ruiz.marisol.lookiest.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NetworkSyncManager(
    context: Context,
    private val outfitRepository: OutfitRepository,
    private val prendaRepository: PrendaRepository,
    private val usoRepository: UsoRepository
) {
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun startListening(scope: CoroutineScope) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                scope.launch {
                    outfitRepository.sincronizarPendientes()
                    prendaRepository.sincronizarPendientes()
                    usoRepository.sincronizarPendientes()
                }
            }
        })
    }
}
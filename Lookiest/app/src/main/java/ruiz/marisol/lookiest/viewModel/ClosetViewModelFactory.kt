package ruiz.marisol.lookiest.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ruiz.marisol.lookiest.data.OutfitRepository
import ruiz.marisol.lookiest.data.PrendaRepository
import ruiz.marisol.lookiest.data.UsoRepository

class ClosetViewModelFactory(
    private val prendaRepository: PrendaRepository,
    private val outfitRepository: OutfitRepository,
    private val usoRepository: UsoRepository,
    private val userEmail: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClosetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClosetViewModel(prendaRepository, outfitRepository, usoRepository, userEmail, context) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}

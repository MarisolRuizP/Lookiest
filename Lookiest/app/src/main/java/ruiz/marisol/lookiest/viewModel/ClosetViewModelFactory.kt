package ruiz.marisol.lookiest.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao
import ruiz.marisol.lookiest.data.OutfitRepository

class ClosetViewModelFactory(
    private val prendaDao: PrendaDao,
    private val outfitRepository: OutfitRepository,
    private val usoDao: UsoOutfitDao,
    private val userEmail: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClosetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClosetViewModel(prendaDao, outfitRepository, usoDao, userEmail, context) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
package ruiz.marisol.lookiest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao
import ruiz.marisol.lookiest.data.DataStoreManager
import ruiz.marisol.lookiest.data.LookiestDatabase
import ruiz.marisol.lookiest.navigation.AppNavigation
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LookiestDatabase.getDatabase(applicationContext)

        val authViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(
                        database.usuarioDao(),
                        DataStoreManager(applicationContext)
                    ) as T
                }
            }
        )[AuthViewModel::class.java]

        setContent {
            val usuarioData by authViewModel.usuarioLogueado.collectAsState()
            val email = usuarioData?.email ?: ""
            val esTemaOscuro = usuarioData?.isDarkMode ?: false

            LookiestTheme(darkTheme = esTemaOscuro) {
                if (email.isNotEmpty()) {
                    val closetViewModel = ViewModelProvider(
                        this,
                        ClosetViewModelFactory(
                            database.prendaDao(),
                            database.outfitDao(),
                            database.usoOutfitDao(),
                            email
                        )
                    )["closet_$email", ClosetViewModel::class.java]

                    AppNavigation(
                        authViewModel   = authViewModel,
                        closetViewModel = closetViewModel
                    )
                } else {
                    AppNavigation(
                        authViewModel   = authViewModel,
                        closetViewModel = ViewModelProvider(
                            this,
                            ClosetViewModelFactory(
                                database.prendaDao(),
                                database.outfitDao(),
                                database.usoOutfitDao(),
                                ""
                            )
                        )["closet_empty", ClosetViewModel::class.java]
                    )
                }
            }
        }
    }
}

class ClosetViewModelFactory(
    private val prendaDao: PrendaDao,
    private val outfitDao: OutfitDao,
    private val usoDao: UsoOutfitDao,
    private val userEmail: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClosetViewModel(prendaDao, outfitDao, usoDao, userEmail) as T
    }
}
package ruiz.marisol.lookiest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import ruiz.marisol.lookiest.data.LookiestDatabase
import ruiz.marisol.lookiest.data.OutfitRepository
import ruiz.marisol.lookiest.navigation.AppNavigation
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModelFactory

@Suppress("UNCHECKED_CAST")
class MainActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LookiestDatabase.getDatabase(applicationContext)

        setContent {
            val currentUser by authViewModel.currentUser.collectAsState()
            val email = currentUser?.email ?: ""
            val esTemaOscuro by authViewModel.isDarkMode.collectAsState()

            LookiestTheme(darkTheme = esTemaOscuro) {

                val outfitRepository = OutfitRepository(
                    outfitDao = database.outfitDao(),
                    context   = applicationContext
                )

                val closetViewModel = ViewModelProvider(
                    this@MainActivity,
                    ClosetViewModelFactory(
                        prendaDao        = database.prendaDao(),
                        outfitRepository = outfitRepository,
                        usoDao           = database.usoOutfitDao(),
                        userEmail        = email,
                        context          = applicationContext
                    )
                )["closet_$email", ClosetViewModel::class.java]

                AppNavigation(
                    authViewModel   = authViewModel,
                    closetViewModel = closetViewModel
                )
            }
        }
    }
}
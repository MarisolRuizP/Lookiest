package ruiz.marisol.lookiest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import ruiz.marisol.lookiest.data.DAO.OutfitDao
import ruiz.marisol.lookiest.data.DAO.PrendaDao
import ruiz.marisol.lookiest.data.DAO.UsoOutfitDao
import ruiz.marisol.lookiest.data.DataStoreManager
import ruiz.marisol.lookiest.data.LookiestDatabase
import ruiz.marisol.lookiest.navigation.AppNavigation
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

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
                if (email.isNotEmpty()) {
                    val closetViewModel = ViewModelProvider(
                        this@MainActivity,
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClosetViewModel(
                                    database.prendaDao(),
                                    database.outfitDao(),
                                    database.usoOutfitDao(),
                                    email
                                ) as T
                            }
                        }
                    )["closet_$email", ClosetViewModel::class.java]

                    AppNavigation(
                        authViewModel   = authViewModel,
                        closetViewModel = closetViewModel
                    )
                } else {
                    val closetViewModel = ViewModelProvider(
                        this@MainActivity,
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ClosetViewModel(
                                    database.prendaDao(),
                                    database.outfitDao(),
                                    database.usoOutfitDao(),
                                    ""
                                ) as T
                            }
                        }
                    )["closet_empty", ClosetViewModel::class.java]

                    AppNavigation(
                        authViewModel   = authViewModel,
                        closetViewModel = closetViewModel
                    )
                }
            }
        }
    }
}
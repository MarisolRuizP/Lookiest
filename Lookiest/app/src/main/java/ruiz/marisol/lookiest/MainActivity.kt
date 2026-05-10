package ruiz.marisol.lookiest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ruiz.marisol.lookiest.data.DataStoreManager
import ruiz.marisol.lookiest.data.LookiestDatabase
import ruiz.marisol.lookiest.navigation.AppNavigation
import ruiz.marisol.lookiest.ui.theme.LookiestTheme
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LookiestDatabase.getDatabase(applicationContext)

        val authViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(database.usuarioDao(),DataStoreManager(applicationContext)) as T
                }
            }
        )[AuthViewModel::class.java]

        val closetViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ClosetViewModel(
                        database.prendaDao(),
                        database.outfitDao(),
                        database.usoOutfitDao()  
                    ) as T
                }
            }
        )[ClosetViewModel::class.java]

        setContent {
            LookiestTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    closetViewModel = closetViewModel
                )
            }
        }
    }
}
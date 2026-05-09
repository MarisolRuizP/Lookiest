package ruiz.marisol.lookiest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ruiz.marisol.lookiest.ui.theme.screens.ClosetScreen
import ruiz.marisol.lookiest.ui.theme.screens.LoginScreen
import ruiz.marisol.lookiest.ui.theme.screens.RegistroScreen
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

//Pantallas
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object MiCloset : Screen("mi_closet")
    object MisOutfits : Screen("mis_outfits")
    object ExplorarOutfits : Screen("explorar_outfits")
    object AgregarPrenda : Screen("agregar_prenda")
    object DetallesPrenda : Screen("detalles_prenda")
    object EditarPrenda : Screen("editar_prenda")
}

@Composable
fun AppNavigation(
    viewModel: AuthViewModel,
    closetViewModel: ClosetViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        //Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Registro.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MiCloset.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        //Pantalla de registro
        composable(Screen.Registro.route) {
            RegistroScreen(
                viewModel = viewModel,
                onRegistrationComplete = {
                    navController.navigate(Screen.MiCloset.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        //Pantalla de MiCloset
        composable(Screen.MiCloset.route) {
            ClosetScreen(
                closetViewModel
            )
        }
    }
}
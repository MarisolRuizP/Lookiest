package ruiz.marisol.lookiest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ruiz.marisol.lookiest.ui.theme.screens.CambiarContraScreen
import ruiz.marisol.lookiest.ui.theme.screens.ClosetScreen
import ruiz.marisol.lookiest.ui.theme.screens.EditarPerfilScreen
import androidx.navigation.navArgument
import ruiz.marisol.lookiest.ui.screens.DetalleOutfitScreen
import ruiz.marisol.lookiest.ui.screens.OutfitsScreen
import ruiz.marisol.lookiest.ui.theme.screens.AgregarPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.CalendarioScreen
import ruiz.marisol.lookiest.ui.theme.screens.ClosetScreen
import ruiz.marisol.lookiest.ui.theme.screens.DetallesPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.EditarPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.LoginScreen
import ruiz.marisol.lookiest.ui.theme.screens.PerfilScreen
import ruiz.marisol.lookiest.ui.theme.screens.RegistroScreen
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

//Pantallas
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object MiCloset : Screen("mi_closet")
    object Perfil : Screen("perfil")
    object EditarPerfil : Screen ("editar_perfil")
    object CambiarContra : Screen("cambiar_contra")
    object AgregarPrenda : Screen("agregar_prenda")
    object DetallesPrenda : Screen("detalles_prenda/{prendaId}") {
        fun createRoute(prendaId: Int) = "detalles_prenda/$prendaId"
    }
    object EditarPrenda : Screen("editar_prenda/{prendaId}") {
        fun createRoute(prendaId: Int) = "editar_prenda/$prendaId"
    }
    object MisOutfits : Screen("mis_outfits")
    object DetallesOutfit : Screen("detalles_outfit/{outfitId}") {
        fun createRoute(outfitId: Int) = "detalles_outfit/$outfitId"
    }

    object Calendario : Screen("calendario")
}


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    closetViewModel: ClosetViewModel
) {
    val navController = rememberNavController()
    val prendas by closetViewModel.prendas.collectAsState(initial = emptyList())
    val outfits by closetViewModel.outfits.collectAsState(initial = emptyList())

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        //login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
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

        //registro
        composable(Screen.Registro.route) {
            RegistroScreen(
                viewModel = authViewModel,
                onRegistrationComplete = {
                    navController.navigate(Screen.MiCloset.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        //closet (pantalla principal)
        composable(Screen.MiCloset.route) {
            ClosetScreen(
                viewModel = closetViewModel,
                navController = navController,
                onPrendaClick = { prenda ->
                    navController.navigate(Screen.DetallesPrenda.createRoute(prenda.id))
                }
            )
        }

        // agregar prenda
        composable(Screen.AgregarPrenda.route) {
            AgregarPrendaScreen(
                viewModel = closetViewModel,
                navController = navController,
                onGuardado = { navController.popBackStack() },
                onDescartado = { navController.popBackStack() }
            )
        }

        // detalles de la prenda
        composable(
            route = Screen.DetallesPrenda.route,
            arguments = listOf(navArgument("prendaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val prendaId = backStackEntry.arguments?.getInt("prendaId") ?: return@composable
            val prenda = prendas.find { it.id == prendaId } ?: return@composable

            DetallesPrendaScreen(
                prenda = prenda,
                viewModel = closetViewModel,
                navController = navController,
                onEditar = { navController.navigate(Screen.EditarPrenda.createRoute(prendaId)) },
                onEliminarConfirmado = { navController.popBackStack() }
            )
        }

        // editar prenda
        composable(
            route = Screen.EditarPrenda.route,
            arguments = listOf(navArgument("prendaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val prendaId = backStackEntry.arguments?.getInt("prendaId") ?: return@composable
            val prenda = prendas.find { it.id == prendaId } ?: return@composable

            EditarPrendaScreen(
                prendaInicial = prenda,
                viewModel = closetViewModel,
                navController = navController,
                onGuardado = { navController.popBackStack() },
                onDescartado = { navController.popBackStack() }
            )
        }

        // mis outfits + explorar outfits
        composable(Screen.MisOutfits.route) {
            OutfitsScreen(
                viewModel = closetViewModel,
                navController = navController,
                onOutfitClick = { outfit ->
                    navController.navigate(Screen.DetallesOutfit.createRoute(outfit.id))
                },
                onNuevoOutfit = { }
            )
        }


        // detalles outfit
        composable(
            route = Screen.DetallesOutfit.route,
            arguments = listOf(navArgument("outfitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getInt("outfitId") ?: return@composable
            val outfit   = outfits.find { it.id == outfitId } ?: return@composable

            DetalleOutfitScreen(
                outfit = outfit,
                viewModel = closetViewModel,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel = authViewModel,
                onNavigateToEdit = { navController.navigate("editar_perfil") },
                onNavigateToChangePass = { navController.navigate("cambiar_contra") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.EditarPerfil.route) {
            EditarPerfilScreen(
                viewModel = authViewModel,
                onNavigateBack = {navController.navigate("perfil")}
            )
        }

        composable(Screen.CambiarContra.route) {
            CambiarContraScreen(
                viewModel = authViewModel,
                onNavigateBack = {navController.navigate("perfil")}
            )
        }

        composable(Screen.Calendario.route) {
            CalendarioScreen(viewModel = closetViewModel)
        }
    }
}

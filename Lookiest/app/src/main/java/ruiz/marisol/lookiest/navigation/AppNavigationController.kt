package ruiz.marisol.lookiest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ruiz.marisol.lookiest.ui.screens.DetalleOutfitScreen
import ruiz.marisol.lookiest.ui.screens.OutfitsScreen
import ruiz.marisol.lookiest.ui.theme.screens.AgregarPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.ClosetScreen
import ruiz.marisol.lookiest.ui.theme.screens.DetallesPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.EditarPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.LoginScreen
import ruiz.marisol.lookiest.ui.theme.screens.RegistroScreen
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

//Pantallas
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object MiCloset : Screen("mi_closet")
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
}


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    closetViewModel: ClosetViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        //login
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

        //registro
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
            val prenda = closetViewModel.prendas.find { it.id == prendaId } ?: return@composable

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
            val prenda = closetViewModel.prendas.find { it.id == prendaId } ?: return@composable

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
            val outfit   = closetViewModel.outfits.find { it.id == outfitId } ?: return@composable

            DetalleOutfitScreen(
                outfit = outfit,
                viewModel = closetViewModel,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }
}

package ruiz.marisol.lookiest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import ruiz.marisol.lookiest.ui.theme.screens.CrearOutfitScreen
import ruiz.marisol.lookiest.ui.theme.screens.DetallesPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.EditarOutfitScreen
import ruiz.marisol.lookiest.ui.theme.screens.EditarPrendaScreen
import ruiz.marisol.lookiest.ui.theme.screens.LoginScreen
import ruiz.marisol.lookiest.ui.theme.screens.OutfitDeHoyScreen
import ruiz.marisol.lookiest.ui.theme.screens.PerfilScreen
import ruiz.marisol.lookiest.ui.theme.screens.RegistroScreen
import ruiz.marisol.lookiest.viewModel.AuthViewModel
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

//Pantallas
sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object Registro       : Screen("registro")
    object MiCloset       : Screen("mi_closet")
    object Perfil         : Screen("perfil")
    object EditarPerfil   : Screen("editar_perfil")
    object CambiarContra  : Screen("cambiar_contra")
    object AgregarPrenda  : Screen("agregar_prenda")
    object MisOutfits     : Screen("mis_outfits")
    object CrearOutfit    : Screen("crear_outfit")
    object OutfitDeHoy    : Screen("outfit_de_hoy")
    object Calendario     : Screen("calendario")

    object DetallesPrenda : Screen("detalles_prenda/{prendaId}") {
        fun createRoute(prendaId: Int) = "detalles_prenda/$prendaId"
    }
    object EditarPrenda : Screen("editar_prenda/{prendaId}") {
        fun createRoute(prendaId: Int) = "editar_prenda/$prendaId"
    }
    object DetallesOutfit : Screen("detalles_outfit/{outfitId}") {
        fun createRoute(outfitId: Int) = "detalles_outfit/$outfitId"
    }
    object EditarOutfit : Screen("editar_outfit/{outfitId}") {
        fun createRoute(outfitId: Int) = "editar_outfit/$outfitId"
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    closetViewModel: ClosetViewModel
) {
    val navController = rememberNavController()
    val prendas by closetViewModel.prendas.collectAsState()
    val outfits by closetViewModel.outfits.collectAsState()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel           = authViewModel,
                onNavigateToForgetPass = {
                    navController.navigate("${Screen.CambiarContra.route}/true")
                },
                onNavigateToRegister = { navController.navigate(Screen.Registro.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.MiCloset.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Registro
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

        // Mi Closet
        composable(Screen.MiCloset.route) {
            ClosetScreen(
                viewModel     = closetViewModel,
                navController = navController,
                onPrendaClick = { prenda ->
                    navController.navigate(Screen.DetallesPrenda.createRoute(prenda.id))
                }
            )
        }

        // Agregar prenda
        composable(Screen.AgregarPrenda.route) {
            AgregarPrendaScreen(
                viewModel     = closetViewModel,
                navController = navController,
                onGuardado    = { navController.popBackStack() },
                onDescartado  = { navController.popBackStack() }
            )
        }

        // Detalles prenda
        composable(
            route     = Screen.DetallesPrenda.route,
            arguments = listOf(navArgument("prendaId") { type = NavType.IntType })
        ) { back ->
            val prendaId = back.arguments?.getInt("prendaId") ?: return@composable
            val prenda   = prendas.find { it.id == prendaId } ?: return@composable
            DetallesPrendaScreen(
                prenda               = prenda,
                viewModel            = closetViewModel,
                navController        = navController,
                onEditar             = { navController.navigate(Screen.EditarPrenda.createRoute(prendaId)) },
                onEliminarConfirmado = { navController.popBackStack() }
            )
        }

        // Editar prenda
        composable(
            route     = Screen.EditarPrenda.route,
            arguments = listOf(navArgument("prendaId") { type = NavType.IntType })
        ) { back ->
            val prendaId = back.arguments?.getInt("prendaId") ?: return@composable
            val prenda   = prendas.find { it.id == prendaId } ?: return@composable
            EditarPrendaScreen(
                prendaInicial = prenda,
                viewModel     = closetViewModel,
                navController = navController,
                onGuardado    = { navController.popBackStack() },
                onDescartado  = { navController.popBackStack() }
            )
        }

        // Mis Outfits
        composable(Screen.MisOutfits.route) {
            OutfitsScreen(
                viewModel     = closetViewModel,
                navController = navController,
                onOutfitClick = { outfit ->
                    navController.navigate(Screen.DetallesOutfit.createRoute(outfit.id))
                },
                onNuevoOutfit = {
                    navController.navigate(Screen.CrearOutfit.route)
                }
            )
        }

        // Crear outfit
        composable(Screen.CrearOutfit.route) {
            CrearOutfitScreen(
                viewModel = closetViewModel,
                onGuardar = { navController.popBackStack() },
                onDescartar = { navController.popBackStack() },
                navController = navController
            )
        }

        // Detalles outfit
        composable(
            route     = Screen.DetallesOutfit.route,
            arguments = listOf(navArgument("outfitId") { type = NavType.IntType })
        ) { back ->
            val outfitId = back.arguments?.getInt("outfitId") ?: return@composable
            val outfit   = outfits.find { it.id == outfitId } ?: return@composable

            DetalleOutfitScreen(
                outfit        = outfit,
                viewModel     = closetViewModel,
                navController = navController,
                onBack        = { navController.popBackStack() },
                onEditar      = {
                    navController.navigate(Screen.EditarOutfit.createRoute(outfitId))
                },
                onEliminar    = {
                    closetViewModel.eliminarOutfit(outfit)
                    navController.popBackStack()
                }

            )
        }

        // Editar outfit
        composable(
            route     = Screen.EditarOutfit.route,
            arguments = listOf(navArgument("outfitId") { type = NavType.IntType })
        ) { back ->
            val outfitId = back.arguments?.getInt("outfitId") ?: return@composable
            val outfit   = outfits.find { it.id == outfitId } ?: return@composable
            EditarOutfitScreen(
                outfitInicial = outfit,
                viewModel = closetViewModel,
                onGuardado = { navController.popBackStack() },
                onDescartado = { navController.popBackStack() },
                navController = navController
            )
        }

        // Outfit de hoy
        composable(Screen.OutfitDeHoy.route) {
            OutfitDeHoyScreen(
                viewModel = closetViewModel,
                navController = navController
            )
        }

        // Perfil
        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel             = authViewModel,
                onNavigateToEdit      = { navController.navigate(Screen.EditarPerfil.route) },
                navController = navController,
                onNavigateToChangePass = { navController.navigate("${Screen.CambiarContra.route}/false") },
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.EditarPerfil.route) {
            EditarPerfilScreen(
                viewModel       = authViewModel,
                onNavigateBack  = { navController.popBackStack() }
            )
        }

        composable("${Screen.CambiarContra.route}/{esOlvido}") { backStackEntry ->
            val esOlvido = backStackEntry.arguments?.getString("esOlvido")?.toBoolean() ?: false

            CambiarContraScreen(
                viewModel = authViewModel,
                esOlvido = esOlvido,
                onNavigateToHome = {navController.navigate(Screen.MiCloset.route)},
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendario.route) {
            CalendarioScreen(
                viewModel = closetViewModel,
                navController = navController
                )
        }
    }
}
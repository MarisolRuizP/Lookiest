package ruiz.marisol.lookiest.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ruiz.marisol.lookiest.navigation.Screen
import ruiz.marisol.lookiest.ui.theme.Azul
import ruiz.marisol.lookiest.ui.theme.BlancoFondo

@Composable
fun LookiestBottomBar(
    selected: Int = 2,
    navController: NavController
) {
    val items = listOf(
        "Outfits"       to Icons.Default.Checkroom,
        "Outfit de Hoy" to Icons.Default.CalendarToday,
        "Inicio"        to Icons.Default.Home,
        "Mi Calendario" to Icons.Default.CalendarMonth,
        "Perfil"        to Icons.Default.Person
    )

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { i, (label, icon) ->
            NavigationBarItem(
                selected = i == selected,
                onClick  = {
                    when (i) {
                        0 -> navController.navigate(Screen.MisOutfits.route)
                        2 -> navController.navigate(Screen.MiCloset.route)
                        // 1, 3, 4 → agregar cuando tengas esas pantallas
                    }
                },
                icon   = { Icon(icon, contentDescription = label) },
                label  = { Text(label, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Azul,
                    selectedTextColor = Azul,
                    indicatorColor    = BlancoFondo
                )
            )
        }
    }
}
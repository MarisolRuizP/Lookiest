package ruiz.marisol.lookiest.ui.theme.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.ui.theme.Azul
import ruiz.marisol.lookiest.ui.theme.BlancoFondo
import ruiz.marisol.lookiest.ui.theme.Rosa50

@Composable
fun LookiestBottomBar(selected: Int = 2) {
    val items = listOf(
        "Outfits" to Icons.Default.Checkroom,
        "Nuevo Outfit" to Icons.Default.AddCircle,
        "Inicio" to Icons.Default.Home,
        "Mi Calendario" to Icons.Default.CalendarMonth,
        "Perfil" to Icons.Default.Person
    )
    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { i, (label, icon) ->
            NavigationBarItem(
                selected = i == selected,
                onClick  = {},
                icon  = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Azul,
                    selectedTextColor = Azul,
                    indicatorColor = BlancoFondo
                )
            )
        }
    }
}
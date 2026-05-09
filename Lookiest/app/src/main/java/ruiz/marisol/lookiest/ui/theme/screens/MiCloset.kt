package ruiz.marisol.lookiest.ui.theme.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ruiz.marisol.lookiest.ui.theme.components.PrendaCard
import ruiz.marisol.lookiest.viewModel.ClosetViewModel

@Composable
fun ClosetScreen(viewModel: ClosetViewModel) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Lookiest",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar...") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mi Closet", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { }) {
                Text(text = "Ordenar por ↕", color = Color(0xFFE57373)) // rojo suave
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.prendas) { item ->
                PrendaCard(
                    prenda = item,
                    onFavoriteClick = { viewModel.favorito(item.id) },
                    onMoreClick = { }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCloset() {
    val closetViewModel: ClosetViewModel = viewModel()
    ClosetScreen(viewModel = closetViewModel)
}
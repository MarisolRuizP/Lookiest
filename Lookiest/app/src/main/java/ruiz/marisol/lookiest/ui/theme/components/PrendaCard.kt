package ruiz.marisol.lookiest.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ruiz.marisol.lookiest.data.PrendaRopa

@Composable
fun PrendaCard(
    prenda: PrendaRopa,
    onFavoriteClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()

    ) {
        Column {
            Box {
                prenda.imagen?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = prenda.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                    )
                }
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (prenda.favorito) Icons.Filled.Star
                        else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (prenda.favorito) Color(0xFFFFC107)
                        else Color.Gray
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prenda.nombre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                }
            }
            Text(
                text = prenda.categoria,
                fontSize = 11.sp,
                color = Color(0xFF5C6BC0),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        color = Color(0xFFE8EAF6),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
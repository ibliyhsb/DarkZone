package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.app.viewmodel.NewsViewModel

@Composable
fun NewsDetailScreen(id: Int?, viewModel: NewsViewModel) {
    val newsList by viewModel.news.collectAsState()
    val article = newsList.find { it.id == id?.toLong() }

    if (article == null) {
        Text("Noticia no encontrada", modifier = Modifier.padding(16.dp))
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text(text = article.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Fuente: ${article.autor}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        Text(text = article.contenido, style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Fecha: ${article.fechaPublicacion}", style = MaterialTheme.typography.bodySmall)
        
        if (!article.imagenUri.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Imagen: ${article.imagenUri}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

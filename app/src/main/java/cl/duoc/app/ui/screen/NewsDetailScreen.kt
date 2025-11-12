package cl.duoc.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.app.viewmodel.NewsViewModel

@Composable
fun NewsDetailScreen(id: Int?, viewModel: NewsViewModel) {
    val context = LocalContext.current
    val article = viewModel.getArticleById(id)

    if (article == null) {
        Text("Noticia no encontrada", modifier = Modifier.padding(16.dp))
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text(text = article.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Fuente: ${article.source}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        Text(text = article.content, style = MaterialTheme.typography.bodyMedium)

        if (!article.url.isNullOrBlank()) {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                context.startActivity(intent)
            }, modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()) {
                Text("Abrir noticia completa")
            }
        }
    }
}

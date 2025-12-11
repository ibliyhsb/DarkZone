package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.viewmodel.GNewsViewModel
import cl.duoc.app.network.GNewsArticle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun TerrorNewsScreen() {
    val apiKey = "a1f22396fb3d345fb726ec2093b9df16"
    val viewModel: GNewsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GNewsViewModel(apiKey) as T
        }
    })
    val articles by viewModel.articles.observeAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchTerrorNews()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Noticias de Terror", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        articles.forEach { article ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                context.startActivity(intent)
            }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(article.title, style = MaterialTheme.typography.titleMedium)
                    article.description?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("Fuente: ${article.source.name}", style = MaterialTheme.typography.bodySmall)
                    Text("Fecha: ${article.publishedAt}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

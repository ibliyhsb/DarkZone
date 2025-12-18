package cl.duoc.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.viewmodel.GNewsViewModel
import android.content.Intent
import android.net.Uri

@Composable
fun NewsScreen() {
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

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Noticias de Terror", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(articles) { article ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                         val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                         context.startActivity(intent)
                    }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                        article.description?.let {
                            Text(text = it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        }
                        Text(text = "Fuente: ${article.source.name}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

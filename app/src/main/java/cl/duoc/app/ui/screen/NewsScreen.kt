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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.app.viewmodel.NewsViewModel

@Composable
fun NewsScreen(viewModel: NewsViewModel, onOpen: (Int) -> Unit) {
    val articles = viewModel.articles.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(articles) { article ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onOpen(article.id) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = article.source, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}
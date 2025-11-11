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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.viewmodel.BlogViewModel

@Composable
fun RecentBlogsScreen(
    viewModel: BlogViewModel,
    onBlogClicked: (Long) -> Unit
) {
    val blogs by viewModel.recentBlogs.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(blogs) { blog ->
            BlogItem(blog) {
                onBlogClicked(blog.id)
            }
        }
    }
}

@Composable
fun BlogItem(blog: FormularioBlogsEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = blog.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "por ${blog.usuarioAutor}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
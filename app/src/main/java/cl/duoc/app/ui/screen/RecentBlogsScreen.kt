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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Temporal data class for demonstration purposes
data class BlogPost(val title: String, val author: String)

@Composable
fun RecentBlogsScreen(onBlogClick: (BlogPost) -> Unit) {
    // This is sample data. In a real app, you would get this from a ViewModel.
    val recentBlogs = listOf(
        BlogPost("El Caleuche: El Barco Fantasma de Chiloé:", "Ana"),
        BlogPost("El Trauco: El Ser de la Noche:", "Luis"),
        BlogPost("La Llorona del Mapocho", "Maria"),
        BlogPost("Catalina de los Ríos y Lisperguer: La Quintrala:", "Carlos"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(recentBlogs) { blog ->
            BlogItem(blog = blog, onClick = { onBlogClick(blog) })
        }
    }
}

@Composable
fun BlogItem(blog: BlogPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = blog.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "por ${blog.author}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

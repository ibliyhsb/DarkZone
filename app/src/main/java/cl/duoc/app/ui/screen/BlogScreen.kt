package cl.duoc.app.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.viewmodel.BlogViewModel

@Composable
fun BlogScreen(
    viewModel: BlogViewModel,
    onNewBlog: () -> Unit,
    onBlogClicked: (Long) -> Unit
) {
    val blogs: List<FormularioBlogsEntity> by viewModel.blogs.collectAsState(initial = emptyList())
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewBlog) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo blog")
            }
        }
    ) { padding ->
        if (blogs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Sin publicaciones aún")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(blogs) { blog ->
                    Card(
                        colors = CardDefaults.cardColors(),
                        modifier = Modifier
                            .fillMaxWidth().clickable { onBlogClicked(blog.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            if (!blog.imagenUri.isNullOrBlank()) {
                                val uri: Uri = blog.imagenUri!!.toUri()
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = blog.titulo,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(blog.titulo, style = MaterialTheme.typography.titleMedium)
                            if (blog.descripcion.isNotBlank()) {
                                Text(
                                    blog.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Text(
                                blog.fechaPublicacion,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
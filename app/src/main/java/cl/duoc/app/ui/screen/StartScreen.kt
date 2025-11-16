package cl.duoc.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.app.R
import cl.duoc.app.viewmodel.BlogViewModel
import cl.duoc.app.viewmodel.NewsViewModel

private data class UiPost(val id: Long, val title: String, val author: String)

@Composable
fun StartScreen(blogViewModel: BlogViewModel, newsViewModel: NewsViewModel) {
    val blogs by blogViewModel.blogs.collectAsState()
    val news by newsViewModel.articles.collectAsState()

    val blogPosts = blogs.map { UiPost(it.id, it.titulo, it.usuarioAutor) }
    val newsPosts = news.map { UiPost(it.id.toLong(), it.title, it.source) }

    Column(Modifier.fillMaxSize()) {
        // Banner
        Image(
            painter = painterResource(id = R.drawable.hero_darkzone),
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Últimos blogs
        Text(
            text = "Últimos blogs",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(blogPosts) { post ->
                PostItem(post)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Noticias
        Text(
            text = "Noticias",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(newsPosts) { post ->
                PostItem(post)
            }
        }
    }
}

@Composable
private fun PostItem(post: UiPost) {
    Card(modifier = Modifier.width(200.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.author, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

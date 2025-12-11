package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.viewmodel.ExternalApiViewModel

@Composable
fun ExternalApiScreen() {
    val viewModel: ExternalApiViewModel = viewModel()
    val items by viewModel.items.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchItems()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Datos de API Externa", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        items.forEach { item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                    if (!item.author.isNullOrBlank()) {
                        Text("Autor: ${item.author}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.story, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

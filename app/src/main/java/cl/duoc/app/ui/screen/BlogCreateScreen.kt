package cl.duoc.app.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import cl.duoc.app.viewmodel.BlogViewModel

@Composable
fun BlogCreateScreen(
    viewModel: BlogViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.blogCreateState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.onImagenUriChange(uri) }
    )

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = state.titulo, onValueChange = { viewModel.onTituloChange(it) }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = state.descripcion, onValueChange = { viewModel.onDescripcionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = state.contenido, onValueChange = { viewModel.onContenidoChange(it) }, label = { Text("Texto del blog") }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp))

        if (state.imagenUri != null) {
            Image(
                painter = rememberAsyncImagePainter(state.imagenUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Seleccionar imagen (opcional)")
            }
            Spacer(Modifier.weight(1f))
            Button(
                enabled = state.titulo.isNotBlank() && state.contenido.isNotBlank(),
                onClick = {
                    viewModel.crearBlog()
                    onSaved()
                }
            ) {
                Text("Publicar")
            }
        }
    }
}

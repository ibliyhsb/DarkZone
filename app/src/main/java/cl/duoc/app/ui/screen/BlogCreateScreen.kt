package cl.duoc.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import cl.duoc.app.viewmodel.BlogViewModel

@Composable
fun BlogCreateScreen(
    viewModel: BlogViewModel,
    onSaved: () -> Unit,
    blogId: Long? = null,
    readOnly: Boolean = false
) {
    val state by viewModel.blogCreateState.collectAsState()
    val selected by viewModel.selectedBlog.collectAsState()

    // If opened for detail, load the blog
    LaunchedEffect(blogId, readOnly) {
        if (readOnly && blogId != null) {
            viewModel.loadBlogById(blogId)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> viewModel.onImagenUriChange(uri) }
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val tituloText = if (readOnly) selected?.titulo ?: "" else state.titulo
        val descripcionText = if (readOnly) selected?.descripcion ?: "" else state.descripcion
        val contenidoText = if (readOnly) selected?.contenido ?: "" else state.contenido

        OutlinedTextField(value = tituloText, onValueChange = { if (!readOnly) viewModel.onTituloChange(it) }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), enabled = !readOnly)
        OutlinedTextField(value = descripcionText, onValueChange = { if (!readOnly) viewModel.onDescripcionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), enabled = !readOnly)
        OutlinedTextField(value = contenidoText, onValueChange = { if (!readOnly) viewModel.onContenidoChange(it) }, label = { Text("Texto del blog") }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp), enabled = !readOnly)

        if (state.titulo.isBlank() || state.contenido.isBlank()) {
            Text(
                text = "El título y el contenido son obligatorios para publicar.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Show image from selected blog (detail) or from current create state
        val imagePainterSource = when {
            readOnly -> selected?.imagenUri
            state.imagenUri != null -> state.imagenUri
            else -> null
        }
        if (imagePainterSource != null) {
            Image(
                painter = rememberAsyncImagePainter(imagePainterSource),
                contentDescription = "Imagen",
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
        }

        if (!readOnly) {
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
}

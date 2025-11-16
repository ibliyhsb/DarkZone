package cl.duoc.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
<<<<<<< HEAD
    val state by viewModel.blogCreateState.collectAsState()
    val selected by viewModel.selectedBlog.collectAsState()

    // If opened for detail, load the blog
    LaunchedEffect(blogId, readOnly) {
        if (readOnly && blogId != null) {
            viewModel.loadBlogById(blogId)
        }
    }
=======
    val blog by viewModel.selectedBlog.collectAsState(initial = null)

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
>>>>>>> 9bea06fa16e55d3e5f2a079aea1e537fe9cada0c

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imagenUri = uri }
    )

    LaunchedEffect(blogId) {
        if (blogId != null && blogId != 0L) {
            viewModel.loadBlogById(blogId)
        }
    }

    LaunchedEffect(blog) {
        if (readOnly && blog != null) {
            blog?.let {
                titulo = it.titulo
                descripcion = it.descripcion
                contenido = it.contenido
                imagenUri = it.imagenUri?.let { Uri.parse(it) }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
<<<<<<< HEAD
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
=======
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), readOnly = readOnly)
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), readOnly = readOnly)
        OutlinedTextField(value = contenido, onValueChange = { contenido = it }, label = { Text("Texto del blog") }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp), readOnly = readOnly)

        if (imagenUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imagenUri),
                contentDescription = if(readOnly) "Imagen del blog" else "Imagen seleccionada",
>>>>>>> 9bea06fa16e55d3e5f2a079aea1e537fe9cada0c
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
<<<<<<< HEAD
                    enabled = state.titulo.isNotBlank() && state.contenido.isNotBlank(),
                    onClick = {
                        viewModel.crearBlog()
=======
                    enabled = titulo.isNotBlank() && contenido.isNotBlank(),
                    onClick = {
                        viewModel.crearBlog(
                            titulo = titulo,
                            descripcion = descripcion,
                            contenido = contenido,
                            imagenUri = imagenUri?.toString()
                        )
>>>>>>> 9bea06fa16e55d3e5f2a079aea1e537fe9cada0c
                        onSaved()
                    }
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}
package cl.duoc.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
    val state by viewModel.blogCreateState.collectAsState()
    val selected by viewModel.selectedBlog.collectAsState()

    // If opened for detail, load the blog
    LaunchedEffect(blogId, readOnly) {
        if (readOnly && blogId != null) {
            viewModel.loadBlogById(blogId)
        }
    }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imagenUri = uri }
    )

    LaunchedEffect(selected) {
        selected?.let {
            titulo = it.titulo
            descripcion = it.descripcion
            contenido = it.contenido
            imagenUri = it.imagenUri?.let { Uri.parse(it) }
        }
    }

    var showConfirmDelete by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val effectiveReadOnly = readOnly && !isEditing

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (effectiveReadOnly) {
            OutlinedTextField(
                value = selected?.titulo ?: "",
                onValueChange = {},
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            OutlinedTextField(
                value = selected?.descripcion ?: "",
                onValueChange = {},
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            OutlinedTextField(
                value = selected?.contenido ?: "",
                onValueChange = {},
                label = { Text("Texto del blog") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                readOnly = true
            )

            selected?.imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(it)),
                    contentDescription = "Imagen del blog",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Mostrar acciones solo para el autor
            selected?.let { blog ->
                if (blog.usuarioAutor == viewModel.usuarioActual) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { isEditing = true }) {
                            Text("Editar")
                        }
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { showConfirmDelete = true }) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            if (showConfirmDelete) {
                AlertDialog(
                    onDismissRequest = { showConfirmDelete = false },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro de que deseas eliminar este blog? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirmDelete = false
                            // Realizar eliminación en ViewModel y volver atrás
                            viewModel.deleteSelectedBlog(onDeleted = { onSaved() })
                        }) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDelete = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        } else {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Texto del blog") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )

            imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Seleccionar imagen (opcional)")
                }
                Spacer(Modifier.weight(1f))
                Button(
                    enabled = titulo.isNotBlank() && contenido.isNotBlank(),
                    onClick = {
                        // Actualizar el estado en el ViewModel antes de crear
                        viewModel.onTituloChange(titulo)
                        viewModel.onDescripcionChange(descripcion)
                        viewModel.onContenidoChange(contenido)
                        viewModel.onImagenUriChange(imagenUri)
                        if (blogId != null) {
                            // Edición de un blog existente
                            viewModel.updateSelectedBlog(titulo, descripcion, contenido, imagenUri?.toString())
                        } else {
                            viewModel.crearBlog()
                        }
                        onSaved()
                    }
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}
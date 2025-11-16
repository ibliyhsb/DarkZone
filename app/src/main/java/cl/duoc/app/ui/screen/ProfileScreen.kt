package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import cl.duoc.app.navigation.Routes
import cl.duoc.app.viewmodel.ProfileViewModel
import cl.duoc.app.viewmodel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(username: String = "usuario_demo", navController: NavController? = null) {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val repository = remember(db) { FormularioUsuarioRepository(db.formularioUsuarioDao()) }
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository, username))

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            snackbarHostState.showSnackbar("Perfil actualizado con éxito")
            viewModel.onSuccessMessageShown()
        }
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess && navController != null) {
            navController.navigate(Routes.REGISTRO) {
                popUpTo(0)
            }
        }
    }

    if (state.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteCancel() },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar tu cuenta permanentemente? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onDeleteConfirm() }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDeleteCancel() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = state.nombreUsuario,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error?.contains("nombre") == true
            )

            OutlinedTextField(
                value = state.correoUsuario,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error?.contains("correo") == true
            )

            OutlinedTextField(
                value = state.passwordUsuario,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardarCambios() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.onDeleteRequest() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar cuenta")
            }
        }
    }
}

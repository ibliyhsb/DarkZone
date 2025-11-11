package cl.duoc.app.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.model.domain.FormularioRegistroErrores
import cl.duoc.app.viewmodel.RegistroViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.domain.RegistroUiState
import cl.duoc.app.viewmodel.RegistroViewModelFactory
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.app.ui.components.InputText
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioRegistroScreen(onNavigateToLogin: () -> Unit = {}) {

    val content = androidx.compose.ui.platform.LocalContext.current
    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(content.applicationContext as Application)
    )

    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.estado.collectLatest {
            if (it.registroExitoso) {
                snackbarHostState.showSnackbar("Registro exitoso!")
                viewModel.onMensajeExitosoMostrado()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Formulario de registro",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            // Campo Nombre
            InputText(
                valor = estado.nombreUsuario,
                error = estado.errores.nombreUsuario,
                label = "Nombre del usuario",
                onChange = viewModel::onNombreChange
            )

            // Campo Correo
            InputText(
                valor = estado.correoUsuario,
                error = estado.errores.correoUsuario,
                label = "Correo electrónico",
                onChange = viewModel::onCorreoChange
            )

            InputText(
                valor = estado.passwordUsuario,
                error = estado.errores.passwordUsuario,
                label = "Contraseña",
                onChange = viewModel::onPasswordChange
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onEnviarFormulario,
                modifier = Modifier.fillMaxWidth(),
                enabled = !estado.errores.tieneErrores()
            ) {
                Text("Registrarse")
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioRegistroScreenPreview() {
    FormularioRegistroScreen()
}

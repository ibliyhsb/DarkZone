package cl.duoc.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import cl.duoc.app.viewmodel.LoginViewModel
import cl.duoc.app.viewmodel.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthenticated: (String) -> Unit,
    onNavigateToRegistro: () -> Unit
) {
    val estado by viewModel.state.collectAsState()
    var pwVisible by remember { mutableStateOf(false) }

    if (estado.sesionActiva) {
        LaunchedEffect(estado.sesionActiva) {
            onAuthenticated(estado.user)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = estado.user,
                onValueChange = viewModel::onUserChange,
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.error != null
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = estado.pass,
                onValueChange = viewModel::onPassChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default,
                trailingIcon = {
                    TextButton(onClick = { pwVisible = !pwVisible }) {
                        Text(if (pwVisible) "Ocultar" else "Mostrar")
                    }
                },
                isError = estado.error != null,
                supportingText = {
                    estado.error?.let {
                        if (!estado.isLoading) {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            if (estado.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Iniciar sesión")
                }

                TextButton(onClick = onNavigateToRegistro) {
                    Text("¿Aun no tienes una cuenta? Registrate")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val repository = remember(db) { FormularioUsuarioRepository(db.formularioUsuarioDao()) }
    // val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(repository))
    // LoginScreen(viewModel = viewModel, onAuthenticated = {}, onNavigateToRegistro = {})
}
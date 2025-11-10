package cl.duoc.app.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.app.viewmodel.RegistroViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioRegistroScreen() {

    val content = androidx.compose.ui.platform.LocalContext.current
    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(content.applicationContext as Application)
    )

    val estado by viewModel.estado.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text(
            text = "Formulario de Registro",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
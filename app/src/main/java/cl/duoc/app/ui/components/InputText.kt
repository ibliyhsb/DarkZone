package cl.duoc.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    valor: String,
    error: String?,
    label: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
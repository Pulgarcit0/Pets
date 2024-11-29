package edu.itvo.pets.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.itvo.pets.data.models.PetModel
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PetDetails(
    pet: PetModel,
    onDelete: () -> Unit,
    onUpdate: () -> Unit, // Nuevo evento para actualizar
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de ${pet.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.List, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Nombre: ${pet.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tipo: ${pet.type}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Raza: ${pet.race}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Descripción: ${pet.description}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha de Nacimiento: ${pet.birthdate}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
                Button(
                    onClick = onUpdate, // Evento de actualización
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Actualizar")
                }
            }
        }
    }
}

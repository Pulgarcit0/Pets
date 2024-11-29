package edu.itvo.pets.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.itvo.pets.presentation.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pet(
    viewModel: PetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var hasSaved by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.NameChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(text = "Nombre:") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.DescriptionChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(text = "Descripción:") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown para el tipo de mascota
            var expanded by remember { mutableStateOf(false) }
            val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.type,
                    onValueChange = { },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Tipo:") },
                    trailingIcon = {
                        Icon(icon, contentDescription = null)
                    },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    state.petTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onEvent(PetViewModel.PetEvent.TypeChanged(selectionOption))
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.race,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.RaceChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(text = "Raza:") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.birthdate,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.BirthdateChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(text = "Fecha de Nacimiento:") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.image,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.ImageChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text(text = "Foto:") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar o actualizar
            Button(
                onClick = {
                    if (state.isEditing) {
                        // Actualización de mascota existente
                        viewModel.onEvent(PetViewModel.PetEvent.UpdateClicked(
                            id = state.selectedPetId ?: 0, // Usa el ID de la mascota seleccionada
                            name = state.name,
                            description = state.description,
                            type = state.type,
                            race = state.race,
                            birthdate = state.birthdate,
                            image = state.image
                        ))
                    } else {
                        // Registro de nueva mascota
                        viewModel.onEvent(PetViewModel.PetEvent.AddClicked(
                            name = state.name,
                            description = state.description,
                            type = state.type,
                            race = state.race,
                            birthdate = state.birthdate,
                            image = state.image
                        ))
                    }
                    hasSaved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (state.isEditing) "Actualizar" else "Guardar")
            }

            if (hasSaved) {
                AlertDialog(
                    onDismissRequest = { hasSaved = false },
                    confirmButton = {
                        Button(onClick = { hasSaved = false }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("Éxito") },
                    text = { Text(if (state.isEditing) "Actualizado satisfactoriamente" else "Guardado satisfactoriamente") }
                )
            }
        }
    }
}

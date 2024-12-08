package edu.itvo.pets.presentation.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import edu.itvo.pets.presentation.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pet(
    viewModel: PetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var hasSaved by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // Para el menú desplegable
    val scrollState = rememberScrollState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // Para la imagen seleccionada
    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    // Lanzador para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
            viewModel.onEvent(PetViewModel.PetEvent.ImageChanged(uri.toString())) // Actualiza el estado
        }
    )

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

            // Dropdown para seleccionar tipo
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
                    trailingIcon = { Icon(icon, contentDescription = null) },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    state.petTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onEvent(PetViewModel.PetEvent.TypeChanged(type))
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

            // Botón para seleccionar imagen
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Seleccionar Imagen")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar imagen seleccionada
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar o actualizar
            Button(
                onClick = {
                    if (state.isEditing) {
                        // Actualización
                        viewModel.onEvent(PetViewModel.PetEvent.UpdateClicked(
                            id = state.selectedPetId ?: 0,
                            name = state.name,
                            description = state.description,
                            type = state.type,
                            race = state.race,
                            birthdate = state.birthdate,
                            image = selectedImageUri.toString()
                        ))
                    } else {
                        // Agregar
                        viewModel.onEvent(PetViewModel.PetEvent.AddClicked(
                            name = state.name,
                            description = state.description,
                            type = state.type,
                            race = state.race,
                            birthdate = state.birthdate,
                            image = selectedImageUri.toString()
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

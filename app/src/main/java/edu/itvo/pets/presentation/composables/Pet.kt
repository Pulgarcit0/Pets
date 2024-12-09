package edu.itvo.pets.presentation.composables

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import edu.itvo.pets.R
import edu.itvo.pets.presentation.viewmodel.PetViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pet(
    viewModel: PetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            uri?.let {
                val savedPath = viewModel.saveImageToInternalStorage(it, context)
                println("Ruta guardada: $savedPath") // Depuración
                viewModel.onEvent(PetViewModel.PetEvent.ImageChanged(savedPath))
            }
        }
    )
    // Calendario para inicializar el date picker
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Crear el DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, yearSelected, monthSelected, daySelected ->
            val formattedDate =
                String.format("%02d/%02d/%04d", daySelected, monthSelected + 1, yearSelected)
            viewModel.onEvent(PetViewModel.PetEvent.BirthdateChanged(formattedDate))
        },
        year, month, day
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = if (state.isEditing) "Actualizar Mascota" else "Registrar Mascota",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Selección de imagen
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.image.isNotEmpty() && File(state.image).exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = File(state.image)),
                        contentDescription = "Imagen de ${state.name}",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar Foto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )

                }

            }
            // Nombre
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.NameChanged(it)) },
                label = { Text(text = "Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                supportingText = {
                    if (state.name.isEmpty()) {
                        Text(text = "Este campo es obligatorio", color = Color.Red)
                    }
                },
                isError = state.name.isEmpty()
            )

            // Descripción
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.DescriptionChanged(it)) },
                label = { Text(text = "Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = false,
                maxLines = 4
            )

            // Tipo (Dropdown)
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
                        .padding(vertical = 4.dp),
                    label = { Text("Tipo") },
                    trailingIcon = { Icon(icon, contentDescription = null) },
                    readOnly = true,
                    singleLine = true,
                    isError = state.type.isEmpty(),
                    supportingText = {
                        if (state.type.isEmpty()) {
                            Text(text = "Selecciona un tipo", color = Color.Red)
                        }
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    state.petTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type) },
                            onClick = {
                                viewModel.onEvent(PetViewModel.PetEvent.TypeChanged(type))
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Raza
            OutlinedTextField(
                value = state.race,
                onValueChange = { viewModel.onEvent(PetViewModel.PetEvent.RaceChanged(it)) },
                label = { Text(text = "Raza") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                supportingText = {
                    if (state.race.isEmpty()) {
                        Text(text = "Este campo es obligatorio", color = Color.Red)
                    }
                },
                isError = state.race.isEmpty()
            )

            // Fecha de Nacimiento
            OutlinedTextField(
                value = state.birthdate,
                onValueChange = { /* No permitir cambios manuales */ },
                label = { Text(text = "Fecha de Nacimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { datePickerDialog.show() },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Seleccionar Fecha",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                },
                singleLine = true,
                supportingText = {
                    if (state.birthdate.isEmpty()) {
                        Text(text = "Selecciona una fecha", color = Color.Red)
                    }
                },
                isError = state.birthdate.isEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Guardar/Actualizar
            Button(
                onClick = {
                    // Validación simple antes de enviar el evento
                    if (state.name.isNotBlank() &&
                        state.type.isNotBlank() &&
                        state.race.isNotBlank() &&
                        state.birthdate.isNotBlank()
                    ) {
                        if (state.isEditing) {
                            viewModel.onEvent(
                                PetViewModel.PetEvent.UpdateClicked(
                                    id = state.selectedPetId ?: 0,
                                    name = state.name,
                                    description = state.description,
                                    type = state.type,
                                    race = state.race,
                                    birthdate = state.birthdate,
                                    image = state.image // Usa el state.image, que es el savedPath
                                )
                            )
                        } else {
                            viewModel.onEvent(
                                PetViewModel.PetEvent.AddClicked(
                                    name = state.name,
                                    description = state.description,
                                    type = state.type,
                                    race = state.race,
                                    birthdate = state.birthdate,
                                    image = state.image // Aquí también usa state.image
                                )
                            )
                        }
                        hasSaved = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (state.isEditing) "Actualizar Mascota" else "Guardar Mascota",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            // Mensaje de éxito
            if (hasSaved) {
                LaunchedEffect(Unit) {
                    // Ocultar el mensaje después de 2 segundos
                    kotlinx.coroutines.delay(2000)
                    hasSaved = false
                }
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = if (state.isEditing) "Mascota actualizada exitosamente" else "Mascota registrada exitosamente",
                        color = Color.White
                    )
                }
            }
        }
    }
}
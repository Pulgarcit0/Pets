package edu.itvo.pets.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import edu.itvo.pets.data.models.PetModel
import coil.compose.rememberAsyncImagePainter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetails(
    pet: PetModel,
    onDelete: () -> Unit,
    onUpdate: () -> Unit, // Evento de actualización
    onBack: () -> Unit
) {
    // Definir los gradientes de los botones
    val deleteGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF5F6D), // Rojo oscuro
            Color(0xFFFFC371)  // Naranja claro
        )
    )

    val updateGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00C6FF), // Azul claro
            Color(0xFF0072FF)  // Azul oscuro
        )
    )

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
            // Imagen de la mascota
            if (pet.image.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = File(pet.image)),
                    contentDescription = "Imagen de ${pet.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                // Botón Eliminar con gradiente
                GradientButton(
                    text = "Eliminar",
                    gradient = deleteGradient,
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    textColor = Color.White,
                    cornerRadius = 24f,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit, // Puedes cambiar el icono si prefieres otro
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // Botón Actualizar con gradiente
                GradientButton(
                    text = "Actualizar",
                    gradient = updateGradient,
                    onClick = onUpdate,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    textColor = Color.White,
                    cornerRadius = 24f,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit, // Puedes cambiar el icono si prefieres otro
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
    }
}

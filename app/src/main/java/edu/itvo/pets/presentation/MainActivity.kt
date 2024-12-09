package edu.itvo.pets.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import edu.itvo.pets.presentation.composables.Pet
import edu.itvo.pets.presentation.composables.PetDetails
import edu.itvo.pets.presentation.composables.PetList
import edu.itvo.pets.presentation.ui.theme.PetsTheme
import edu.itvo.pets.presentation.viewmodel.PetViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetsTheme {
                MainScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PetViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val currentScreen = remember { mutableStateOf(Screen.List) } // Estado para alternar entre pantallas

    // Si la actualización fue exitosa, regresa automáticamente a la lista
    if (state.success) {
        currentScreen.value = Screen.List
        viewModel.onEvent(PetViewModel.PetEvent.Reset) // Restablece el estado
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen.value) {
                            Screen.List -> "Lista de Mascotas"
                            Screen.Register -> "Registrar Mascota"
                            Screen.Details -> "Detalles de Mascota"
                        }
                    )
                },
                navigationIcon = {
                    if (currentScreen.value != Screen.List) {
                        IconButton(onClick = { currentScreen.value = Screen.List }) {
                            Icon(imageVector = Icons.Filled.List, contentDescription = "Volver")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentScreen.value == Screen.List) {
                FloatingActionButton(
                    onClick = { currentScreen.value = Screen.Register }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar")
                }
            }
        },
        content = { innerPadding ->
            when (currentScreen.value) {
                Screen.List -> PetList(
                    modifier = Modifier.padding(innerPadding),
                    pets = state.pets,
                    onPetClick = { pet ->
                        viewModel.onEvent(PetViewModel.PetEvent.SelectPet(pet))
                        currentScreen.value = Screen.Details
                    }

                )
                Screen.Register -> Pet(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel
                )
                Screen.Details -> {
                    state.selectedPet?.let { pet ->
                        PetDetails(
                            pet = pet,
                            onDelete = {
                                viewModel.onEvent(PetViewModel.PetEvent.DeletePet(pet))
                                currentScreen.value = Screen.List
                            },
                            onUpdate = {
                                viewModel.onEvent(PetViewModel.PetEvent.LoadPetForUpdate(pet)) // Precarga datos
                                currentScreen.value = Screen.Register
                            },
                            onBack = { currentScreen.value = Screen.List }
                        )
                    }
                }
            }
        }
    )
}

// Enum para definir las pantallas
enum class Screen {
    List, Register, Details
}


package edu.itvo.pets.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.itvo.pets.core.utils.JsonUtils
import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.domain.usecases.PetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(private val petUseCase: PetUseCase,
                                       private val application: Application // Necesitamos el contexto de la aplicación
):  ViewModel(){
    private val _uiState = MutableStateFlow(PetState())
    val uiState: StateFlow<PetState> = _uiState.asStateFlow()
    init {
        // Carga inicial de las mascotas
        onEvent(PetEvent.LoadPets)
        // Carga los tipos de mascotas
        loadPetTypes()
    }

    private fun loadPetTypes() {
        val petTypes = JsonUtils.getPetTypes(application.applicationContext)
        _uiState.value = _uiState.value.copy(petTypes = petTypes)
    }
    fun onEvent(event: PetEvent) {
        when (event) {
            is PetEvent.UpdateClicked -> {
                val updatedPet = PetModel(
                    id = event.id,
                    name = event.name,
                    description = event.description,
                    type = event.type,
                    race = event.race,
                    birthdate = event.birthdate,
                    image = event.image
                )
                viewModelScope.launch(Dispatchers.IO) {
                    petUseCase.updatePet(updatedPet)
                    onEvent(PetEvent.LoadPets) // Recarga la lista
                    _uiState.value = _uiState.value.copy(success = true) // Marca como exitoso
                }
            }




            is PetEvent.LoadPetForUpdate -> {
                _uiState.value = _uiState.value.copy(
                    name = event.pet.name,
                    description = event.pet.description,
                    type = event.pet.type,
                    race = event.pet.race,
                    birthdate = event.pet.birthdate,
                    image = event.pet.image,
                    isEditing = true,
                    selectedPetId = event.pet.id
                )
            }


            is PetEvent.DeletePet -> {
                viewModelScope.launch(Dispatchers.IO) {
                    petUseCase.deletePet(event.pet)
                    deleteImage(event.pet.image) // Agrega esta línea para eliminar la imagen

                    onEvent(PetEvent.LoadPets) // Recarga la lista
                }
            }

            is PetEvent.SelectPet -> {  //seleccion de
                _uiState.value = _uiState.value.copy(selectedPet = event.pet)
            }

            is PetEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    name = event.name,
                    touchedFields = _uiState.value.touchedFields.apply { add("name") }
                )
            }
            is PetEvent.DescriptionChanged -> {
                _uiState.value = _uiState.value.copy(
                    description = event.description,
                    touchedFields = _uiState.value.touchedFields.apply { add("description") }
                )
            }
            is PetEvent.ImageChanged -> {
                _uiState.value = _uiState.value.copy(
                    image =  event.image)
            }
            is PetEvent.TypeChanged -> {
                _uiState.value = _uiState.value.copy(
                    type = event.type,
                    touchedFields = _uiState.value.touchedFields.apply { add("type") }
                )
            }
            is PetEvent.RaceChanged -> {
                _uiState.value = _uiState.value.copy(
                    race = event.race,
                    touchedFields = _uiState.value.touchedFields.apply { add("race") }
                )
            }
            is PetEvent.BirthdateChanged -> {
                _uiState.value = _uiState.value.copy(
                    birthdate = event.birthdate,
                    touchedFields = _uiState.value.touchedFields.apply { add("birthdate") }
                )
            }
            is PetEvent.AddClicked -> {
                val pet = PetModel(
                    id = 0,
                    name = event.name,
                    description = event.description,
                    type = event.type,
                    race = event.race,
                    birthdate = event.birthdate,
                    image = event.image // Aquí debe estar la ruta de la imagen
                )
                viewModelScope.launch(Dispatchers.IO) {
                    petUseCase.addPet(pet)
                    onEvent(PetEvent.LoadPets) // Recarga la lista después de guardar
                }
            }


            is PetEvent.Reset -> {
                _uiState.value = _uiState.value.copy(
                    name = "",
                    description = "",
                    type = "",
                    race = "",
                    birthdate = "",
                    image = "",
                    isEditing = false,
                    selectedPetId = null,
                    success = false // Limpia el estado de éxito
                )
            }

            is PetEvent.LoadPets -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                    try {
                        val response = petUseCase.getPets().first() // Obtén los datos
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            pets = response?.data?.filterNotNull() ?: emptyList()
                        )
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar mascotas"
                        )
                    }
                }
            }


            else -> {}
        }
    }
    data class PetState(
        val name: String = "",
        val description: String = "",
        val image: String = "",
        val type: String = "",
        val race: String = "",
        val birthdate: String = "",
        val isLoading: Boolean = false,
        val error: String = "",
        val success: Boolean = false,
        val hasError: Boolean = false,
        val pets: List<PetModel> = emptyList(),
        val petTypes: List<String> = emptyList(),
        val selectedPet: PetModel? = null,
        val selectedPetId: Int? = null,
        val isEditing: Boolean = false,
        val touchedFields: MutableSet<String> = mutableSetOf() // Campos tocados
    )

    sealed class PetEvent {
        data class NameChanged(val name: String) : PetEvent()
        data class DescriptionChanged(val description: String) : PetEvent()
        data class ImageChanged(val image: String) : PetEvent()
        data class TypeChanged(val type: String) : PetEvent()
        data class RaceChanged(val race: String) : PetEvent()
        data class BirthdateChanged(val birthdate: String) : PetEvent()
        data class AddClicked(val name: String,
                              val description: String,
                              val type: String,
                              val race: String,
                              val birthdate: String,
                              val image: String
        ) : PetEvent()
        data class UpdateClicked(
            val id: Int,
            val name: String,
            val description: String,
            val type: String,
            val race: String,
            val birthdate: String,
            val image: String
        ) : PetEvent()

        data object Reset : PetEvent()
        object LoadPets : PetEvent()
        data class SelectPet(val pet: PetModel) : PetEvent() // Nuevo evento
        data class DeletePet(val pet: PetModel) : PetEvent()
        data class LoadPetForUpdate(val pet: PetModel) : PetEvent() // Para cargar datos en modo edición



    }
    @HiltViewModel
    class PetViewModel @Inject constructor(
        private val petUseCase: PetUseCase,
        private val application: Application
    ) : ViewModel() {

    }
    fun saveImageToInternalStorage(uri: Uri, context: Context): String {
        val file = File(context.filesDir, "images")
        if (!file.exists()) {
            file.mkdir() // Crear directorio si no existe
        }
        val fileName = "pet_image_${System.currentTimeMillis()}.jpg"
        val imageFile = File(file, fileName)

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            println("Imagen guardada en: ${imageFile.absolutePath}") // Línea para depuración
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al guardar la imagen: ${e.message}") // Depuración en caso de error
        }

        return imageFile.absolutePath // Devuelve la ruta absoluta
    }

    fun deleteImage(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }



}
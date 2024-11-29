package edu.itvo.pets.domain.usecases

import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.data.models.PetResponse
import edu.itvo.pets.domain.PetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PetUseCase @Inject constructor(private val petRepository: PetRepository) {
    suspend fun getPets(): Flow<PetResponse?> = petRepository.getPets()
    suspend fun addPet(petModel: PetModel) = petRepository.addPet(petModel)
    suspend fun deletePet(petModel: PetModel) = petRepository.deletePet(petModel)
    suspend fun updatePet(petModel: PetModel) = petRepository.updatePet(petModel) // Define este método


}


package edu.itvo.pets.domain

import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.data.models.PetResponse
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    suspend fun getPets(): Flow<PetResponse?>
    suspend fun getPetRandom(): Flow<PetResponse?>
    suspend fun getPet(petId:Int): Flow<PetResponse?>
    suspend fun updatePet(petModel: PetModel)
    suspend fun addPet(petModel: PetModel)
    suspend fun addPets(pets:List<PetModel>)
    suspend fun deletePet(petModel: PetModel)


}
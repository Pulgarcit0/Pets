package edu.itvo.pets.data

import edu.itvo.pets.data.local.PetLocalDataSource
import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.data.models.PetResponse
import edu.itvo.pets.domain.PetRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class PetRepositoryImpl
    @Inject constructor (private val localDataSource: PetLocalDataSource):
    PetRepository {
    override suspend  fun getPets(): Flow<PetResponse?> {

        return localDataSource.getPets()
    }

    override suspend fun getPetRandom(): Flow<PetResponse?> {
        return  localDataSource.getPetRandom()
    }

    override suspend fun getPet(petId: Int): Flow<PetResponse> {
        return localDataSource.getPet(petId)
    }

    override suspend fun updatePet(petModel: PetModel) = localDataSource.update(petModel) // Delegación a DataSource


    override suspend fun addPet (petModel: PetModel) {
        localDataSource.insert(petModel)
    }

    override suspend fun addPets(pets: List<PetModel>) {
        localDataSource.insertAll(pets)
    }
    override suspend fun deletePet(petModel: PetModel) {
        localDataSource.delete(petModel)
    }

}
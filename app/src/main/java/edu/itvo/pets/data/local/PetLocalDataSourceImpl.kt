package edu.itvo.pets.data.local

import edu.itvo.pets.core.utils.toEntity
import edu.itvo.pets.core.utils.toListPetModel
import edu.itvo.pets.core.utils.toPetModel
import edu.itvo.pets.data.local.daos.PetDao
import edu.itvo.pets.data.models.PetModel
import edu.itvo.pets.data.models.PetResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PetLocalDataSourceImpl
    @Inject constructor(private val petDao: PetDao): PetLocalDataSource {
    override suspend fun getPets(): Flow<PetResponse?> {
        val pets = petDao.getPets()
        val data = pets.map { it.toListPetModel() }
        val listPetModel =data.first()
        return flow { emit(
            PetResponse(true, "list pets", listPetModel)) }
    }

    override suspend fun getPet(petId: Int): Flow<PetResponse> {
        val pet= petDao.getPet(petId).map { it.toPetModel() }.first()
        return  flow { emit(
            PetResponse(true, "list pets", listOf(pet))) }

    }

    override suspend fun getPetRandom(): Flow<PetResponse> {
        val pet= petDao.getPetRandom().map { it.toPetModel() }.first()
        return  flow { emit(
            PetResponse(true, "list pets", listOf(pet))) }
    }


    override suspend  fun insertAll(pets: List<PetModel>) {
        petDao.insertAll(pets.map { it.toEntity()})
    }


    override suspend  fun insert(pet: PetModel) {
        petDao.insert(pet.toEntity())
    }


    override suspend fun update(pet: PetModel) {
        petDao.update (pet.toEntity())
    }

    override suspend fun delete(pet: PetModel) {
        petDao.delete(pet.toEntity())
    }




}
package edu.itvo.pets.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.itvo.pets.data.local.entities.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pet: PetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pets: List<PetEntity>)

    @Query("SELECT * FROM pet ORDER BY name ASC")
    fun getPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pet WHERE id = :petId")
    fun getPet(petId: Int): Flow<PetEntity>

    @Query("SELECT * FROM pet ORDER BY random() LIMIT 1")
    fun getPetRandom(): Flow<PetEntity>

    @Query("DELETE FROM pet")
    fun deleteAll(): Int

    @Query("DELETE FROM pet WHERE id=:petId ")
    fun delete(petId: Int): Int

    @Update
    fun update(pet: PetEntity): Int

    @Delete
    fun delete(pet: PetEntity)




}
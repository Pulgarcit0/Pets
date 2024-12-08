package edu.itvo.pets.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.itvo.pets.core.utils.Converters
import edu.itvo.pets.data.local.daos.PetDao
import edu.itvo.pets.data.local.entities.PetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(entities = [PetEntity::class,  ], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PetDB : RoomDatabase() {

    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: PetDB? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PetDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetDB::class.java,
                    "pets.dbf"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PetDBCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PetDBCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //--- Si se desea conservar los datos mediante reinicios de la aplicación,
                //--- comentar las siguientes líneas.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populate(database.petDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more elements
         */
        suspend fun populate(petDao: PetDao) {

            petDao.deleteAll()

            val pet = PetEntity(
                id = 1,
                name = "Chispitas",
                birthdate = LocalDateTime.now().toString(),
                description = "Perro blanco con cafe, patitas blancas",
                race = "Pitbull",
                image = "hugo"
            )
            petDao.insert(pet)

        }
    }
}
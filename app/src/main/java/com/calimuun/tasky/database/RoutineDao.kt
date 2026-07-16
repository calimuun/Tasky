package com.calimuun.tasky.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/*
* This is a DAO (Data Access Object)
*
* Their function is to perform operations on the database
*
* As you can see, there is some annotations that define default operations (@Insert, @Update) or a @Query that defines a custom operations
* */
@Dao
interface RoutineDao {
	@Insert
	suspend fun insert(routine: RoutineEntity)

	@Update
	suspend fun update(routine: RoutineEntity)

	@Query("DELETE FROM sqlite_sequence WHERE name = 'Routine'")
	suspend fun resetId()

	@Query("SELECT * FROM Routine")
	fun getAllRoutines(): Flow<List<RoutineEntity>>

	@Query("DELETE FROM Routine WHERE id = :id")
	suspend fun deleteRoutine(id: Int)

	@Query("DELETE FROM Routine")
	suspend fun deleteAllRoutines()

	@Query("UPDATE Routine SET date = :date, checked = :checked WHERE id = :id")
	suspend fun updateRoutineCheck(id: Int, date: LocalDate, checked: Boolean)
}
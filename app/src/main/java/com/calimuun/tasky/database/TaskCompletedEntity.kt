package com.calimuun.tasky.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/*
* This defines an entity (aka table) on the database
* */
@Entity(
	tableName = "TaskCompleted"
)
data class TaskCompletedEntity (
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "title") val title: String,
	@ColumnInfo(name = "description") val description: String?,
	@ColumnInfo(name = "date") val date: LocalDate?,
	@ColumnInfo(name = "time") val time: LocalTime?
)
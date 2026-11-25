package com.example.notesapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,

    @ColumnInfo(defaultValue = "''")
    val note: String = ""
)

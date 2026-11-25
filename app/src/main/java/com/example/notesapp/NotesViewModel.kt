package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) :
    AndroidViewModel(application) {

    private val dao =
        AppDatabase.getInstance(application).noteDao()

    // READ
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
    fun addNote(title: String, note: String) {
        val titleText = title.trim()
        val noteText = note.trim()

        if (titleText.isEmpty() || noteText.isEmpty()) return

        viewModelScope.launch {
            dao.insert(
                Note(
                    title = titleText,
                    note = noteText
                )
            )
        }
    }

    // UPDATE
    fun updateNote(
        note: Note,
        newTitle: String,
        newNote: String
    ) {
        val titleText = newTitle.trim()
        val noteText = newNote.trim()

        if (titleText.isEmpty() || noteText.isEmpty()) return

        viewModelScope.launch {
            dao.update(
                note.copy(
                    title = titleText,
                    note = noteText
                )
            )
        }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
        }
    }
}

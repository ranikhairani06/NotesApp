
package com.example.notesapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.ui.theme.NotesAppTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
) {
    val notes by vm.notes.collectAsState()

    // State form tambah
    var titleInput by rememberSaveable {
        mutableStateOf("")
    }

    var noteInput by rememberSaveable {
        mutableStateOf("")
    }

    // State form edit
    var editingId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editingTitle by rememberSaveable {
        mutableStateOf("")
    }

    var editingNote by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Catatan Saya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // FORM TAMBAH
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    if (titleInput.isNotBlank() &&
                        noteInput.isNotBlank()
                    ) {
                        vm.addNote(titleInput, noteInput)
                        titleInput = ""
                        noteInput = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Tambah",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("Tambah")
            }
        }

        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // DAFTAR CATATAN
        if (notes.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Belum ada catatan",
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                items(notes, key = { it.id }) { note ->

                    if (editingId == note.id) {

                        // MODE EDIT
                        NoteItemEditing(
                            title = editingTitle,
                            note = editingNote,

                            onTitleChange = {
                                editingTitle = it
                            },

                            onNoteChange = {
                                editingNote = it
                            },

                            onSave = {
                                vm.updateNote(
                                    note,
                                    editingTitle,
                                    editingNote
                                )
                                editingId = null
                            },

                            onCancel = {
                                editingId = null
                            }
                        )

                    } else {

                        // MODE TAMPIL
                        NoteItem(
                            note = note,

                            onEdit = {
                                editingId = note.id
                                editingTitle = note.title
                                editingNote = note.note
                            },

                            onDelete = {
                                vm.deleteNote(note)
                            }
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}

// ITEM CATATAN
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(
                text = note.title,
                fontWeight = FontWeight.SemiBold
            )
        },

        supportingContent = {
            Column {
                Text(note.note)

                Text(
                    "ID: ${note.id}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },

        trailingContent = {
            Row {

                // Tombol Edit
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_edit),
                        "Edit",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Tombol Hapus
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_delete),
                        "Hapus",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    )
}

// FORM EDIT CATATAN
@Composable
fun NoteItemEditing(
    title: String,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Tombol Simpan
            IconButton(
                onClick = onSave,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_check),
                    "Simpan",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tombol Batal
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_close),
                    "Batal",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NotesAppTheme {
        NoteItem(
            note = Note(
                id = 1,
                title = "Belajar Kotlin",
                note = "Mempelajari Room Database dan Jetpack Compose"
            )
        )
    }
}

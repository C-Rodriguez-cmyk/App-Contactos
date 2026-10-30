package com.my.first.app_contactos.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.my.first.app_contactos.model.Contact
import com.my.first.app_contactos.model.Note
import com.my.first.app_contactos.screens.AddContactScreen
import com.my.first.app_contactos.screens.AddNoteScreen
import com.my.first.app_contactos.screens.HomeScreen
import com.my.first.app_contactos.screens.LoginScreen
import com.my.first.app_contactos.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val notes = remember { mutableStateListOf<Note>() }
    val contacts = remember { mutableStateListOf<Contact>() }

    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }
    var initialContactIdForNote by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser
    val startScreen = if (currentUser != null) "home" else "login"

    // Sincronización en tiempo real con Firestore para Notas y Contactos
    LaunchedEffect(auth.currentUser?.uid) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Sincronizar Notas
            db.collection("users").document(userId).collection("notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val fetchedNotes = snapshot.documents.mapNotNull { doc ->
                            try {
                                val note = doc.toObject(Note::class.java)
                                note?.copy(id = doc.id)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        notes.clear()
                        notes.addAll(fetchedNotes)
                    }
                }

            // Sincronizar Contactos
            db.collection("users").document(userId).collection("contacts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val fetchedContacts = snapshot.documents.mapNotNull { doc ->
                            val contact = doc.toObject(Contact::class.java)
                            contact?.copy(id = doc.id)
                        }
                        contacts.clear()
                        contacts.addAll(fetchedContacts)
                    }
                }
        }
    }

    NavHost(navController = navController, startDestination = startScreen) {

        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                notes = notes,
                contacts = contacts,
                onAddNoteClick = {
                    noteToEdit = null
                    initialContactIdForNote = null
                    navController.navigate("add_note")
                },
                onEditNote = { note ->
                    noteToEdit = note
                    initialContactIdForNote = note.contactId
                    navController.navigate("add_note")
                },
                onAddContactClick = {
                    contactToEdit = null
                    navController.navigate("add_contact")
                },
                onEditContact = { contact ->
                    contactToEdit = contact
                    navController.navigate("add_contact")
                },
                onToggleNoteCompleted = { noteToToggle ->
                    val updatedNote = noteToToggle.copy(isCompleted = !noteToToggle.isCompleted)
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("notes")
                            .document(updatedNote.id)
                            .set(updatedNote)
                    }
                    val idx = notes.indexOfFirst { it.id == updatedNote.id }
                    if (idx != -1) {
                        notes[idx] = updatedNote
                    }
                },
                onNavigateToLogin = {
                    notes.clear()
                    contacts.clear()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("add_note") {
            AddNoteScreen(
                noteToEdit = noteToEdit,
                contacts = contacts,
                initialContactId = initialContactIdForNote,
                onBack = { navController.popBackStack() },
                onSaveNote = { savedNote ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("notes")
                            .document(savedNote.id)
                            .set(savedNote)
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error Firestore (Guardar Nota): ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    val existingIndex = notes.indexOfFirst { it.id == savedNote.id }
                    if (existingIndex != -1) {
                        notes[existingIndex] = savedNote
                    } else {
                        notes.add(0, savedNote)
                    }
                },
                onDeleteNote = { noteToDelete ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("notes")
                            .document(noteToDelete.id)
                            .delete()
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error Firestore (Eliminar Nota): ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    notes.remove(noteToDelete)
                }
            )
        }

        composable("add_contact") {
            val currentContactId = contactToEdit?.id
            val linkedNotes = if (currentContactId != null) {
                notes.filter { it.contactId == currentContactId }
            } else emptyList()

            AddContactScreen(
                contactToEdit = contactToEdit,
                linkedNotes = linkedNotes,
                onBack = { navController.popBackStack() },
                onSaveContact = { savedContact ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("contacts")
                            .document(savedContact.id)
                            .set(savedContact)
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error Firestore (Guardar Contacto): ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    val existingIndex = contacts.indexOfFirst { it.id == savedContact.id }
                    if (existingIndex != -1) {
                        contacts[existingIndex] = savedContact.copy(id = savedContact.id)
                    } else {
                        contacts.add(0, savedContact)
                    }
                },
                onDeleteContact = { contactToDelete ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("contacts")
                            .document(contactToDelete.id)
                            .delete()
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error Firestore (Eliminar Contacto): ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    contacts.remove(contactToDelete)
                },
                onAddNoteClick = {
                    noteToEdit = null
                    initialContactIdForNote = contactToEdit?.id
                    navController.navigate("add_note")
                },
                onEditNoteClick = { note ->
                    noteToEdit = note
                    initialContactIdForNote = note.contactId
                    navController.navigate("add_note")
                },
                onToggleNoteCompleted = { noteToToggle ->
                    val updatedNote = noteToToggle.copy(isCompleted = !noteToToggle.isCompleted)
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("notes")
                            .document(updatedNote.id)
                            .set(updatedNote)
                    }
                    val idx = notes.indexOfFirst { it.id == updatedNote.id }
                    if (idx != -1) {
                        notes[idx] = updatedNote
                    }
                }
            )
        }
    }
}

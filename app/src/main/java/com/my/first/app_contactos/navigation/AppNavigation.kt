package com.my.first.app_contactos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val notes = remember { mutableStateListOf<Note>() }
    val contacts = remember { mutableStateListOf<Contact>() }

    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser
    val startScreen = if (currentUser != null) "home" else "login"

    LaunchedEffect(auth.currentUser?.uid) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val fetchedNotes = snapshot.documents.mapNotNull { doc ->
                            val note = doc.toObject(Note::class.java)
                            note?.copy(id = doc.id)
                        }
                        notes.clear()
                        notes.addAll(fetchedNotes)
                    }
                }

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
                    navController.navigate("add_note")
                },
                onEditNote = { note ->
                    noteToEdit = note
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
                onBack = { navController.popBackStack() },
                onSaveNote = { savedNote ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("notes")
                            .document(savedNote.id)
                            .set(savedNote)
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
                    }
                    notes.remove(noteToDelete)
                }
            )
        }

        composable("add_contact") {
            AddContactScreen(
                contactToEdit = contactToEdit,
                onBack = { navController.popBackStack() },
                onSaveContact = { savedContact ->
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).collection("contacts")
                            .document(savedContact.id)
                            .set(savedContact)
                    }
                    val existingIndex = contacts.indexOfFirst { it.id == savedContact.id }
                    if (existingIndex != -1) {
                        contacts[existingIndex] = savedContact
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
                    }
                    contacts.remove(contactToDelete)
                }
            )
        }
    }
}

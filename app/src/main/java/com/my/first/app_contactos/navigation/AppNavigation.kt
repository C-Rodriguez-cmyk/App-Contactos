package com.my.first.app_contactos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.my.first.app_contactos.model.Note
import com.my.first.app_contactos.screens.AddNoteScreen
import com.my.first.app_contactos.screens.HomeScreen
import com.my.first.app_contactos.screens.LoginScreen
import com.my.first.app_contactos.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val notes = remember { mutableStateListOf<Note>() }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }

    // Verificar si ya hay un usuario logueado en Firebase
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startScreen = if (currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startScreen) {

        // Ruta de Login
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

        // Ruta de Registro
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Ruta Principal (Home - Lista de Notas)
        composable("home") {
            HomeScreen(
                notes = notes,
                onAddNoteClick = {
                    noteToEdit = null
                    navController.navigate("add_note")
                },
                onEditNote = { note ->
                    noteToEdit = note
                    navController.navigate("add_note")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Ruta de Formulario (Ver, Editar o Eliminar Nota)
        composable("add_note") {
            AddNoteScreen(
                noteToEdit = noteToEdit,
                onBack = { navController.popBackStack() },
                onSaveNote = { savedNote ->
                    val existingIndex = notes.indexOfFirst { it.id == savedNote.id }
                    if (existingIndex != -1) {
                        notes[existingIndex] = savedNote
                    } else {
                        notes.add(0, savedNote)
                    }
                },
                onDeleteNote = { noteToDelete ->
                    notes.remove(noteToDelete)
                }
            )
        }
    }
}

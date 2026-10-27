package com.my.first.app_contactos.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.first.app_contactos.model.Note
import com.my.first.app_contactos.ui.theme.blanco
import com.my.first.app_contactos.ui.theme.celeste
import com.my.first.app_contactos.ui.theme.negro
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteToEdit: Note? = null,
    onBack: () -> Unit,
    onSaveNote: (Note) -> Unit,
    onDeleteNote: ((Note) -> Unit)? = null
) {
    val isEditing = noteToEdit != null
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Detalle de Nota" else "Nueva Nota",
                        fontWeight = FontWeight.Bold,
                        color = negro
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = negro
                        )
                    }
                },
                actions = {
                    if (isEditing && onDeleteNote != null && noteToEdit != null) {
                        IconButton(
                            onClick = {
                                onDeleteNote(noteToEdit)
                                Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Nota",
                                tint = Color(0xFFE53935)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F6F8)
                )
            )
        },
        containerColor = Color(0xFFF4F6F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // Fecha de la nota en la vista de detalle
            if (isEditing && noteToEdit != null && noteToEdit.date.isNotBlank()) {
                Text(
                    text = "Creado/Modificado: ${noteToEdit.date}",
                    fontSize = 12.sp,
                    color = celeste,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Título de la nota
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Título de la nota", color = Color.Gray, fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = blanco,
                    unfocusedContainerColor = blanco,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = negro,
                    unfocusedTextColor = negro
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido / Descripción
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Escribe aquí el contenido de tu nota...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = blanco,
                    unfocusedContainerColor = blanco,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = negro,
                    unfocusedTextColor = negro
                ),
                maxLines = 15
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lógica y Función Guardar Nota
            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        val currentDate = java.text.SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date())

                        val savedNote = Note(
                            id = noteToEdit?.id ?: UUID.randomUUID().toString(),
                            title = title.ifBlank { "Sin título" },
                            content = content,
                            date = currentDate
                        )
                        onSaveNote(savedNote)
                        Toast.makeText(
                            context,
                            if (isEditing) "Nota actualizada" else "Nota guardada",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Por favor escribe un título o contenido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = celeste)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = blanco
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEditing) "Guardar Cambios" else "Guardar Nota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = blanco
                    )
                }
            }
        }
    }
}

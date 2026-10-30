package com.my.first.app_contactos.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.first.app_contactos.model.Contact
import com.my.first.app_contactos.model.Note
import com.my.first.app_contactos.ui.theme.Gris
import com.my.first.app_contactos.ui.theme.blanco
import com.my.first.app_contactos.ui.theme.celeste
import com.my.first.app_contactos.ui.theme.negro
import com.my.first.app_contactos.utils.formatPhoneNumberWithCountryCode
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    contactToEdit: Contact? = null,
    linkedNotes: List<Note> = emptyList(),
    onBack: () -> Unit,
    onSaveContact: (Contact) -> Unit,
    onDeleteContact: ((Contact) -> Unit)? = null,
    onAddNoteClick: (() -> Unit)? = null,
    onEditNoteClick: ((Note) -> Unit)? = null,
    onToggleNoteCompleted: ((Note) -> Unit)? = null
) {
    val isEditing = contactToEdit != null
    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(contactToEdit?.phone ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDeleteDialog && isEditing && onDeleteContact != null && contactToEdit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = blanco,
            title = { Text("Eliminar contacto", fontWeight = FontWeight.Bold, color = negro) },
            text = { Text("¿Estás seguro de que deseas eliminar a ${contactToEdit.name.ifBlank { "este contacto" }}?", color = negro) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteContact(contactToEdit)
                        Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = negro, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Detalle de Contacto" else "Nuevo Contacto",
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
                    if (isEditing && onDeleteContact != null && contactToEdit != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Contacto",
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.trim().isNotBlank()) nameError = null
                },
                label = { Text("Nombre Completo") },
                placeholder = { Text("Ej. Juan Pérez") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = celeste)
                },
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(nameError!!, color = Color(0xFFE53935), fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = blanco,
                    unfocusedContainerColor = blanco,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = Gris.copy(alpha = 0.5f),
                    focusedTextColor = negro,
                    unfocusedTextColor = negro,
                    focusedLabelColor = negro,
                    unfocusedLabelColor = Gris,
                    errorBorderColor = Color(0xFFE53935),
                    errorLabelColor = Color(0xFFE53935)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    if (it.trim().isNotBlank()) phoneError = null
                },
                label = { Text("Teléfono") },
                placeholder = { Text("Ej. 8888-8888") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = celeste)
                },
                isError = phoneError != null,
                supportingText = {
                    if (phoneError != null) {
                        Text(phoneError!!, color = Color(0xFFE53935), fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = blanco,
                    unfocusedContainerColor = blanco,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = Gris.copy(alpha = 0.5f),
                    focusedTextColor = negro,
                    unfocusedTextColor = negro,
                    focusedLabelColor = negro,
                    unfocusedLabelColor = Gris,
                    errorBorderColor = Color(0xFFE53935),
                    errorLabelColor = Color(0xFFE53935)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            if (phone.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            val formattedPhone = formatPhoneNumberWithCountryCode(phone)
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$formattedPhone")
                            }
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Realizar llamada",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Llamar", color = negro, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val formattedPhone = formatPhoneNumberWithCountryCode(phone)
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:$formattedPhone")
                            }
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar mensaje",
                            tint = celeste,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mensaje", color = negro, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val cleanDigits = phone.replace(Regex("[^0-9]"), "")
                    val isNameValid = name.trim().isNotBlank()
                    val isPhoneValid = phone.trim().isNotBlank() && cleanDigits.length >= 8

                    nameError = if (!isNameValid) "El nombre es obligatorio" else null
                    phoneError = when {
                        phone.trim().isBlank() -> "El teléfono es obligatorio"
                        cleanDigits.length < 8 -> "El teléfono debe tener al menos 8 dígitos"
                        else -> null
                    }

                    if (isNameValid && isPhoneValid) {
                        val formattedPhone = formatPhoneNumberWithCountryCode(phone)
                        val savedContact = Contact(
                            id = contactToEdit?.id ?: UUID.randomUUID().toString(),
                            name = name.trim(),
                            phone = formattedPhone
                        )
                        onSaveContact(savedContact)
                        Toast.makeText(
                            context,
                            if (isEditing) "Contacto actualizado" else "Contacto guardado",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Por favor completa correctamente los campos requeridos", Toast.LENGTH_SHORT).show()
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Guardar Cambios" else "Guardar Contacto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = blanco
                    )
                }
            }

            // Sección Notely: Notas, Tareas y Recordatorios vinculados al contacto
            if (isEditing) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Notas y Recordatorios 📝",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = negro
                                )
                                Text(
                                    text = "Pendientes con esta persona",
                                    fontSize = 12.sp,
                                    color = Gris
                                )
                            }

                            if (onAddNoteClick != null) {
                                TextButton(onClick = onAddNoteClick) {
                                    Text("+ Nueva Nota", color = celeste, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (linkedNotes.isEmpty()) {
                            Text(
                                text = "No hay notas o tareas vinculadas a este contacto.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            linkedNotes.forEach { note ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onEditNoteClick?.invoke(note) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (note.isTask) {
                                            Checkbox(
                                                checked = note.isCompleted,
                                                onCheckedChange = { onToggleNoteCompleted?.invoke(note) },
                                                colors = CheckboxDefaults.colors(checkedColor = celeste)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = note.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (note.isCompleted) Color.Gray else negro,
                                                textDecoration = if (note.isCompleted) TextDecoration.LineThrough else null
                                            )
                                            if (note.content.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = note.content,
                                                    fontSize = 13.sp,
                                                    color = Gris,
                                                    maxLines = 2
                                                )
                                            }
                                            if (note.reminderDate?.isNotBlank() == true) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "⏰ Recordatorio: ${note.reminderDate}",
                                                    fontSize = 11.sp,
                                                    color = celeste,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

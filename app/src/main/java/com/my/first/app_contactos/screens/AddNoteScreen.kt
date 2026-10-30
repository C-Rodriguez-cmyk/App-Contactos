package com.my.first.app_contactos.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.my.first.app_contactos.model.Contact
import com.my.first.app_contactos.model.Note
import com.my.first.app_contactos.ui.theme.blanco
import com.my.first.app_contactos.ui.theme.celeste
import com.my.first.app_contactos.utils.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteToEdit: Note? = null,
    contacts: List<Contact> = emptyList(),
    initialContactId: String? = null,
    onBack: () -> Unit,
    onSaveNote: (Note) -> Unit,
    onDeleteNote: ((Note) -> Unit)? = null
) {
    val isEditing = noteToEdit != null
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    var isTask by remember { mutableStateOf(noteToEdit?.isTask ?: (initialContactId != null)) }
    var isCompleted by remember { mutableStateOf(noteToEdit?.isCompleted ?: false) }
    var selectedContactId by remember { mutableStateOf(noteToEdit?.contactId ?: initialContactId) }
    var reminderDate by remember { mutableStateOf(noteToEdit?.reminderDate ?: "") }
    var reminderTime by remember { mutableStateOf(noteToEdit?.reminderTime ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            reminderTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            reminderDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val selectedContact = contacts.find { it.id == selectedContactId }

    if (showDeleteDialog && isEditing && onDeleteNote != null && noteToEdit != null) {
        DeleteNoteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                NotificationScheduler.cancelNotification(context, noteToEdit.id)
                onDeleteNote(noteToEdit)
                Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Detalle de Nota / Tarea" else "Nueva Nota / Tarea",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (isEditing && onDeleteNote != null && noteToEdit != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Nota",
                                tint = Color(0xFFE53935)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (isEditing && noteToEdit != null && noteToEdit.date.isNotBlank()) {
                val formattedDate = noteToEdit.date.substringBefore(",")
                Text(
                    text = "Creado: $formattedDate",
                    fontSize = 12.sp,
                    color = celeste,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Título (ej. Entregarle el documento)", color = Color.Gray, fontSize = 16.sp) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            ContactPickerDropdown(
                contacts = contacts,
                selectedContact = selectedContact,
                onContactSelected = { selectedContactId = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            TaskReminderSection(
                isTask = isTask,
                isCompleted = isCompleted,
                reminderDate = reminderDate,
                reminderTime = reminderTime,
                onTaskToggle = { checked ->
                    isTask = checked
                    if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                },
                onCompletedToggle = { isCompleted = it },
                onDateClick = { datePickerDialog.show() },
                onTimeClick = { timePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Escribe el contenido o los detalles de la tarea aquí...", color = Color.Gray) },
                label = { Text("Contenido / Detalles") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = celeste,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(java.util.Date())

                        val savedNote = Note(
                            id = noteToEdit?.id ?: UUID.randomUUID().toString(),
                            title = title.ifBlank { "Sin título" },
                            content = content,
                            date = currentDate,
                            contactId = selectedContactId,
                            contactName = selectedContact?.name,
                            isTask = isTask,
                            isCompleted = isCompleted,
                            reminderDate = reminderDate.trim().ifBlank { null },
                            reminderTime = reminderTime.trim().ifBlank { null }
                        )

                        NotificationScheduler.scheduleNotification(context, savedNote)

                        onSaveNote(savedNote)
                        Toast.makeText(
                            context,
                            if (isEditing) "Nota actualizada" else "Nota guardada con recordatorio",
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
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = blanco)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Guardar Cambios" else "Guardar Nota / Recordatorio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = blanco
                    )
                }
            }
        }
    }
}

@Composable
fun ContactPickerDropdown(
    contacts: List<Contact>,
    selectedContact: Contact?,
    onContactSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = celeste)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedContact?.name?.let { "Contacto: $it" } ?: "Vincular con un contacto (Opcional)",
                        color = if (selectedContact != null) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin contacto vinculado") },
                onClick = {
                    onContactSelected(null)
                    expanded = false
                }
            )
            contacts.forEach { contact ->
                DropdownMenuItem(
                    text = { Text(contact.name.ifBlank { "Sin nombre" }) },
                    onClick = {
                        onContactSelected(contact.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskReminderSection(
    isTask: Boolean,
    isCompleted: Boolean,
    reminderDate: String,
    reminderTime: String,
    onTaskToggle: (Boolean) -> Unit,
    onCompletedToggle: (Boolean) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = celeste)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Es una Tarea / Recordatorio", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }
            Switch(
                checked = isTask,
                onCheckedChange = onTaskToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = blanco,
                    checkedTrackColor = celeste
                )
            )
        }

        if (isTask) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = reminderDate,
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Fecha") },
                        label = { Text("Fecha") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = celeste)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = celeste,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        singleLine = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { onDateClick() }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Hora") },
                        label = { Text("Hora") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = celeste)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = celeste,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        singleLine = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { onTimeClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCompletedToggle(!isCompleted) }
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onCompletedToggle,
                    colors = CheckboxDefaults.colors(checkedColor = celeste)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Marcar como tarea completada",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DeleteNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Eliminar nota", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = { Text("¿Estás seguro de que deseas eliminar esta nota?", color = MaterialTheme.colorScheme.onSurface) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    )
}

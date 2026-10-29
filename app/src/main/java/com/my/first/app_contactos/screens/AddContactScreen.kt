package com.my.first.app_contactos.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.first.app_contactos.model.Contact
import com.my.first.app_contactos.ui.theme.blanco
import com.my.first.app_contactos.ui.theme.celeste
import com.my.first.app_contactos.ui.theme.negro
import java.util.UUID

/**
 * Implementar vista de detalle y edición del contacto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    contactToEdit: Contact? = null,
    onBack: () -> Unit,
    onSaveContact: (Contact) -> Unit,
    onDeleteContact: ((Contact) -> Unit)? = null
) {
    val isEditing = contactToEdit != null
    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(contactToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(contactToEdit?.email ?: "") }
    var address by remember { mutableStateOf(contactToEdit?.address ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Diálogo de confirmación para CP10 (Eliminar contacto)
    if (showDeleteDialog && isEditing && onDeleteContact != null && contactToEdit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar contacto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar a ${contactToEdit.name.ifBlank { "este contacto" }}?") },
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
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Contacto" else "Nuevo Contacto",
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
            // Nombre del contacto (CP08 / CP09)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                placeholder = { Text("Ej. Juan Pérez") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = celeste)
                },
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

            Spacer(modifier = Modifier.height(14.dp))

            // Teléfono (CP08 / CP09)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                placeholder = { Text("Ej. 8888-8888") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = celeste)
                },
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("Ej. correo@ejemplo.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = celeste)
                },
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dirección / Notas
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección / Notas") },
                placeholder = { Text("Ej. San José, Costa Rica") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = celeste)
                },
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
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Guardar (CP08: Crear contacto / CP09: Editar contacto)
            Button(
                onClick = {
                    if (name.isNotBlank() || phone.isNotBlank()) {
                        val savedContact = Contact(
                            id = contactToEdit?.id ?: UUID.randomUUID().toString(),
                            name = name.ifBlank { "Sin Nombre" },
                            phone = phone,
                            email = email,
                            address = address
                        )
                        onSaveContact(savedContact)
                        Toast.makeText(
                            context,
                            if (isEditing) "Contacto actualizado" else "Contacto guardado",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Por favor ingresa al menos el nombre o teléfono", Toast.LENGTH_SHORT).show()
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
        }
    }
}

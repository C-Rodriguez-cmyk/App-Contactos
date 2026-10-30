package com.my.first.app_contactos.model

import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val contactId: String? = null,
    val contactName: String? = null,

    @get:PropertyName("isTask")
    @set:PropertyName("isTask")
    var isTask: Boolean = false,

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val reminderDate: String? = null,
    val reminderTime: String? = null
)

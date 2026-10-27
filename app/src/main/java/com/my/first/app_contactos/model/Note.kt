package com.my.first.app_contactos.model

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

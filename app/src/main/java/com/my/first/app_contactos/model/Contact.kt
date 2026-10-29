package com.my.first.app_contactos.model

import java.util.UUID

data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "vault_items")
data class VaultEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val encryptedUsername: String = "",
    val encryptedEmail: String = "",
    val encryptedPassword: String = "",
    val encryptedWebsite: String = "",
    val category: String = "OTHER",
    val itemType: String = "LOGIN",
    val encryptedNotes: String = "",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis()
)

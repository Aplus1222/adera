package com.example.model

data class PasswordHealth(
    val score: Int = 100,
    val safeCount: Int = 0,
    val weakCount: Int = 0,
    val reusedCount: Int = 0,
    val compromisedCount: Int = 0,
    val oldCount: Int = 0,
    val totalCount: Int = 0
)

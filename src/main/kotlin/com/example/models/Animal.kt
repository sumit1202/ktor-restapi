package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Animal(
    val id: Int,
    val name: String
)

package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AnimalRequest(
    val id: Int,
    val name: String
)

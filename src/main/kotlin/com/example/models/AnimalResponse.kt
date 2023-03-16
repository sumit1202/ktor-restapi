package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AnimalResponse<T>(
    val data: T,
    val success: Boolean
)

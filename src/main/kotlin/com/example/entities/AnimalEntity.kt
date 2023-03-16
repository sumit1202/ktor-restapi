package com.example.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object AnimalEntity: Table<Nothing>("animal") {
    val id = int("id").primaryKey()
    val name = text("name")

}
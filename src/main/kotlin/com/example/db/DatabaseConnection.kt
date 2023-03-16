package com.example.db

import org.ktorm.database.Database

object DatabaseConnection {
    //connecting postgresql...
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/animals",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "toor"
    )
}
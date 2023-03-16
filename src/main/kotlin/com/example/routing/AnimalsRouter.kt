package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.AnimalEntity
import com.example.models.Animal
import com.example.models.AnimalRequest
import com.example.models.AnimalResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Application.animalsRoutes() {

    val db = DatabaseConnection.database

    routing {
        //get-animals
        get("/animals") {
            val animals = db.from(AnimalEntity).select()
                .map {
                    val id = it[AnimalEntity.id]
                    val name = it[AnimalEntity.name]
                    Animal(id ?: -1, name ?: "")
                }
            call.respond(animals)
        }

        //post-animals
        post("/animals") {
            val request = call.receive<AnimalRequest>()
            val result = db.insert(AnimalEntity) {
                set(it.id, request.id)
                set(it.name, request.name)
            }
            if (result == 1) {
                //send successful response to client...
                call.respond(
                    HttpStatusCode.OK, AnimalResponse(
                        success = true,
                        data = "values have been successfully inserted..."
                    )
                )
            } else {
                //send failure response to client...
                call.respond(
                    HttpStatusCode.BadRequest, AnimalResponse(
                        success = false,
                        data = "failed to insert values..."
                    )
                )
            }
        }

        //get-animals by id
        get("/animals/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val animal = db.from(AnimalEntity)
                .select()
                .where { AnimalEntity.id eq id }
                .map {
                    val id = it[AnimalEntity.id]!!
                    val name = it[AnimalEntity.name]!!
                    Animal(id = id, name = name)
                }.firstOrNull()

            if (animal == null) {

                call.respond(
                    HttpStatusCode.NotFound, AnimalResponse(
                        success = false,
                        data = "couldn't find animal with id = $id..."
                    )
                )
            } else {

                call.respond(
                    HttpStatusCode.OK, AnimalResponse(
                        success = true,
                        data = animal
                    )
                )
            }

        }

        //put(updating) animals by id
        put("/animals/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val updatedAnimal = call.receive<AnimalRequest>()
            val rowsEffected = db.update(AnimalEntity) {
                set(it.name, updatedAnimal.name)
                where { it.id eq id }
            }

            if (rowsEffected == 1) {
                call.respond(
                    HttpStatusCode.OK, AnimalResponse(
                        success = true,
                        data = "animal has been successfully updated..."
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, AnimalResponse(
                        success = false,
                        data = "animal was not updated, failed..."
                    )
                )
            }


        }

        //delete animals by id
        delete("/animals/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val rowsEffected = db.delete(AnimalEntity) {
                it.id eq id
            }

            if (rowsEffected == 1) {
                call.respond(
                    HttpStatusCode.OK, AnimalResponse(
                        success = true,
                        data = "animal with id = $id, has been successfully deleted..."
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, AnimalResponse(
                        success = false,
                        data = "animal with id = $id doesn't exist, delete failed..."
                    )
                )
            }
        }

    }
}
package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.UserEntity
import com.example.models.AnimalResponse
import com.example.models.User
import com.example.models.UserCredentials
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {
        // post-register
        post("/register") {
            //reading input from post body
            val userCredentials = call.receive<UserCredentials>()

            //input validation
            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AnimalResponse(
                        success = false,
                        data = "username should be greater than or equal to 3 and " +
                                "password should be greater than or equal to 6"
                    )
                )
                return@post
            }

            val username = userCredentials.username.lowercase()
            val password = userCredentials.hashedPassword()

            //check if entered username already exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map { it[UserEntity.username] }
                .firstOrNull()

            if (user != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AnimalResponse(
                        success = false,
                        data = "username already exists, pls use different username..."
                    )
                )
                return@post
            }


            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }

            call.respond(
                HttpStatusCode.Created,
                AnimalResponse(
                    success = true,
                    data = "username has been successfully created..."
                )
            )

        }

        // post-login
        post("/login") {
            //reading input from post body
            val userCredentials = call.receive<UserCredentials>()

            //input validation
            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AnimalResponse(
                        success = false,
                        data = "username should be greater than or equal to 3 and " +
                                "password should be greater than or equal to 6"
                    )
                )
                return@post
            }

            val username = userCredentials.username.lowercase()
            val password = userCredentials.password

            //check if user exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password = it[UserEntity.password]!!
                    User(id, username, password)
                }.firstOrNull()

            //check user
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AnimalResponse(
                        success = false,
                        data = "invalid username or password..."
                    )
                )
                return@post
            }

            //check password
            val doesPasswordMatch = BCrypt.checkpw(password, user?.password)
            if (!doesPasswordMatch) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AnimalResponse(success = false, data = "invalid username or password...")
                )
                return@post
            }

            val token = tokenManager.generateJwtToken(user)

            call.respond(
                HttpStatusCode.OK,
                AnimalResponse(success = true, data = token))
        }


        authenticate {
            // post-me
            get("/me") {
                val principal_ = call.principal<JWTPrincipal>()
                val username = principal_!!.payload.getClaim("username").asString()
                val userid = principal_!!.payload.getClaim("userid").asInt()
                call.respondText("Hello $username with $userid...")

            }

        }

    }
}
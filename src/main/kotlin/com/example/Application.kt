package com.example

import com.example.plugins.configureJwtAuth
import com.example.plugins.configureSerialization
import com.example.routing.animalsRoutes
import com.example.routing.authenticationRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    configureJwtAuth()
    configureSerialization()
    authenticationRoutes()
    animalsRoutes()

}
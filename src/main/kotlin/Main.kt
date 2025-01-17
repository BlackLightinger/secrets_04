package com.example

import com.example.routes.configureRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8084 ) {
        configureSerialization()
        configureRoutes()
    }.start(wait = true)
}

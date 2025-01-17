package com.example.routes

import com.example.services.EmailService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import maskAsPassword
import kotlin.random.Random

var adminPassword = "123123"
val verificationCodes = mutableMapOf<String, String>()

fun Application.configureRoutes() {
    val emailService = EmailService()

    routing {
        get("/status") {
            call.respond(HttpStatusCode.OK)
        }

        post("/login") {
            println("Login endpoint hit!")
            val request = call.receive<LoginRequest>()
            if (request.email != emailService.adminEmail || request.password != adminPassword) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid email or password"))
                return@post
            }
            //val code = generateCode()
            val code = "123456"
            println(adminPassword.maskAsPassword())
            println(code.maskAsPassword())
            verificationCodes[request.email] = code
            emailService.sendEmail(request.email, code)
            call.respond(mapOf("message" to "Verification code sent to email"))
        }

        post("/verify") {
            val request = call.receive<VerifyRequest>()
            val expectedCode = verificationCodes[request.email]

            if (expectedCode == null || expectedCode != request.code) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid verification code"))
                return@post
            }

            verificationCodes.remove(request.email)
            call.respond(mapOf("message" to "Auth success!"))
        }

        post("/reset_password") {
            val request = call.receive<ResetPasswordRequest>()
            if (request.email != emailService.adminEmail || request.oldPassword != adminPassword) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid email or password"))
                return@post
            }

           //val code = generateCode()
            val code = "123456"
            println(code.maskAsPassword())
            verificationCodes[request.email] = code
            emailService.sendEmail(request.email, code)

            call.respond(mapOf("message" to "Password reset code sent to email"))
        }

        post("/verify_reset_password") {
            val request = call.receive<VerifyResetRequest>()
            val expectedCode = verificationCodes[request.email]

            if (expectedCode == null || expectedCode != request.code) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid verification code"))
                return@post
            }

            adminPassword = request.newPassword
            verificationCodes.remove(request.email)
            call.respond(mapOf("message" to "Password changed successfully!"))
        }
    }
}

fun generateCode(): String = Random.nextInt(100000, 999999).toString()

@kotlinx.serialization.Serializable
data class LoginRequest(val email: String, val password: String)

@kotlinx.serialization.Serializable
data class VerifyRequest(val email: String, val code: String)

@kotlinx.serialization.Serializable
data class ResetPasswordRequest(val email: String, val oldPassword: String)

@kotlinx.serialization.Serializable
data class VerifyResetRequest(val email: String, val code: String, val newPassword: String)

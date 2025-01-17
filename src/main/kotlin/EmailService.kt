package com.example.services

import io.github.cdimascio.dotenv.dotenv
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService {
    private val dotenv = dotenv()
    val adminEmail: String = dotenv["RECIPIENT_EMAIL_ADDRESS"] ?: error("RECIPIENT_EMAIL_ADDRESS not set")
    private val senderEmail: String = dotenv["SENDER_EMAIL_ADDRESS"] ?: error("SENDER_EMAIL_ADDRESS not set")
    private val senderPassword: String = dotenv["SENDER_EMAIL_PASSWORD"] ?: error("SENDER_EMAIL_PASSWORD not set")
    private val smtpHost: String = dotenv["SMTP_SERVER"] ?: error("SMTP_SERVER not set")
    private val smtpPort: Int = dotenv["SMTP_PORT"]?.toInt() ?: 587

    fun sendEmail(recipientEmail: String, code: String) {
        val properties = System.getProperties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(senderEmail))
            addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
            subject = "Your Verification Code"
            setText("Your verification code is: $code")
        }

        Transport.send(message)
    }
}

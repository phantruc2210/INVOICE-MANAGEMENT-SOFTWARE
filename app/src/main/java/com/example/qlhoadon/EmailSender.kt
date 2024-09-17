package com.example.qlhoadon

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender
{
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val SMTP_AUTH = "true"
    private const val SMTP_STARTTLS = "true"

    fun sendEmail(fromEmail: String, fromPassword: String, toEmail: String, subject: String, body: String)
    {
        val props = Properties().apply{
            put("mail.smtp.auth", SMTP_AUTH)
            put("mail.smtp.starttls.enable", SMTP_STARTTLS)
            put("mail.smtp.host", SMTP_HOST)
            put("mail.smtp.port", SMTP_PORT)
        }

        val session = Session.getInstance(props, object : Authenticator()
        {
            override fun getPasswordAuthentication(): PasswordAuthentication
            {
                return PasswordAuthentication(fromEmail, fromPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                setSubject(subject)
                setText(body)
            }
            Transport.send(message)
            println("Email đã được gửi thành công")
        } catch (e: MessagingException)
        {
            e.printStackTrace()
            println("Không gửi được email: ${e.message}")
        }
    }
}

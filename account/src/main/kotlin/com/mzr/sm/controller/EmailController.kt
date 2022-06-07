package com.mzr.sm.controller

import com.mzr.sm.service.EmailServiceImpl
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController(
    private val emailServiceImpl: EmailServiceImpl,
) {

    @PostMapping("/email")
    fun updateEmail(newEmail: String) {
        emailServiceImpl.updateEmail(newEmail)
    }

}
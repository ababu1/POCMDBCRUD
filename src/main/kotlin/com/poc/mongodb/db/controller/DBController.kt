package com.poc.mongodb.db.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DBController {
    @GetMapping("/")
    fun Hello() : String {
        return "Hello Kotlin spring boot service"
    }
}
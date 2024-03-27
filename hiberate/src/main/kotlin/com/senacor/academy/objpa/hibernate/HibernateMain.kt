package com.senacor.academy.objpa.hibernate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @GetMapping("/answer", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getAnswer() = "42"
}

@SpringBootApplication
class HibernateMain

fun main(args: Array<String>) {
    runApplication<HibernateMain>(*args)
}

package com.senacor.academy.objpa.springjdbc

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller(
    @Qualifier("jdbcTemplate") // <- switch to the new jdbcClient
    private val pizzaService: PizzaService
) {

    @GetMapping("/answer", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getAnswer() = "42"

    @GetMapping("/pizzas")
    fun getPizzas() = pizzaService.getPizzas()

    @GetMapping("/orders/summary")
    fun getOrdersSummary() = pizzaService.getOrdersSummary()

    @GetMapping("/pizzas/toppings")
    fun getToppings() = pizzaService.getToppings()
}

@SpringBootApplication
class SpringJdbcMain

fun main(args: Array<String>) {
    runApplication<SpringJdbcMain>(*args)
}

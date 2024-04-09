package com.senacor.academy.objpa.springjdbc

import java.time.LocalDate

interface PizzaService {

    fun getPizzas(): List<PizzaResultJavaStyle>
    fun getToppings(): List<ToppingResult>
    fun getOrdersSummary(): List<OrderSummaryResult>
}

data class OrderSummaryResult(
    val orderId: Long,
    val orderDate: LocalDate,
    val customerFirstName: String,
    val customerLastName: String,
    val items: List<OrderSummaryResultItem>
)

data class OrderSummaryResultItem(
    val pizza: PizzaResult,
    val amount: Int
)

data class ToppingResult(
    val topping: String,
    val pizzaName: String
)

data class PizzaResult(
    val id: Long,
    val name: String
)

data class PizzaResultJavaStyle(
    var id: Long,
    var name: String
) {
    @Suppress("unused")
    constructor() : this(0, "") // no-arg constructor for BeanPropertyRowMapper
}

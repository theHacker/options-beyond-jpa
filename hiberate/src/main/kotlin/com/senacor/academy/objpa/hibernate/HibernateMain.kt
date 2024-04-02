package com.senacor.academy.objpa.hibernate

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class Controller(private val pizzaService: PizzaService) {

    @GetMapping("/answer", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getAnswer() = "42"

    @GetMapping("/pizzas")
    fun getPizzas() = pizzaService.getPizzas()

    @GetMapping("/orders/summary")
    fun getOrdersSummary() = pizzaService.getOrdersSummary()

    @PostMapping("/orders/new")
    fun createBuggyOrder() = pizzaService.createBuggyOrder()
}

@Service
class PizzaService(private val entityManager: EntityManager) {

    fun getPizzas(): List<Pizza> = entityManager
        .createQuery("select p from Pizza p", Pizza::class.java)
        .resultList

    fun getOrdersSummary(): List<OrderSummaryResult> = entityManager
        .createQuery(
            "select so " +
                "from ShopOrder so " +
                "join so.customer c " +
                "join fetch so.items " +
                "join so.items soi " +
                "join soi.pizza p",
            ShopOrder::class.java
        )
        .resultList
        .map { row ->
            OrderSummaryResult(
                row.id!!,
                row.date,
                row.customer.firstName,
                row.customer.lastName,
                row.items.map {
                    OrderSummaryResultItem(
                        PizzaResult(it.pizza.id, it.pizza.name),
                        it.amount
                    )
                })
        }

    data class PizzaResult(
        val id: Long,
        val name: String
    )

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

    @Transactional
    fun createBuggyOrder(): ShopOrder {
        val pizzas = entityManager
            .createQuery("from Pizza", Pizza::class.java) // This ain't JPA, but it's shorter and works in Hibernate
            .resultList

        val customer = entityManager
            .createQuery("from Customer", Customer::class.java)
            .setMaxResults(1)
            .singleResult

        // Let's create a new order
        val shopOrder = ShopOrder(null, customer, LocalDate.now(), emptyList())
            .also { entityManager.persist(it) }

        // Save order items
        val orderItems = listOf(
            ShopOrderItem(null, shopOrder, pizzas[0], 2),
            ShopOrderItem(null, shopOrder, pizzas[1], 3),
        )
        orderItems.forEach { entityManager.persist(it) }

        return shopOrder // oops… forgot to update the passive side, so returning empty items list
    }
}

@SpringBootApplication
class HibernateMain

fun main(args: Array<String>) {
    runApplication<HibernateMain>(*args)
}

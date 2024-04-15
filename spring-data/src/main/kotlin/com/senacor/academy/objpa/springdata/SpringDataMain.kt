package com.senacor.academy.objpa.springdata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.sql.Date


@RestController
class Controller(
    private val pizzaService: PizzaService,
    private val orderService: OrderService
) {

    @GetMapping("/answer", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getAnswer() = "42"

    @GetMapping("/pizzas")
    fun getPizzas() = pizzaService.getPizzas()

    @GetMapping("/orders/summary")
    fun getOrdersSummary() = orderService.getOrdersSummary()

    @GetMapping("/pizzas/toppings")
    fun getToppings() = pizzaService.getToppings()
}

// Entity mapping

data class Pizza(
    @Id
    val id: Long,
    val name: String,
    @MappedCollection(idColumn = "PIZZA_ID") // "n+1 select" problem, even when nobody uses the toppings columns
    val toppings: Set<PizzaTopping>
)

data class PizzaTopping(
    val pizzaId: Long,
    val topping: String
)

// Entity mapping for orders. Here we deliberately not map the sub-objects to not query so much.

@Table("SHOP_ORDER")
data class Order(
    @Id
    val id: Long,
    val customerId: Long, // deliberately not mapping sub-object
    val date: Date // <- map as java.SQL.date, so we can easily use toLocalDate()
    // not mapping items here, let's build a separate repository for them
)

@Table("SHOP_ORDER_ITEM")
data class OrderItem(
    @Id
    val id: Long,
    @Column("SHOP_ORDER_ID")
    val orderId: Long, // deliberately not mapping sub-object
    val pizzaId: Long, // deliberately not mapping sub-object
    val amount: Int
)

data class Customer(
    @Id
    val id: Long,
    val firstName: String,
    val lastName: String
)

// Interface used for projection

interface PizzaDescription {
    // Java-style getters
    //
    // fun getId(): Long
    // fun getName(): String

    // works with Kotlin interfaces, too :-)
    val id: Long
    val name: String
}

// Magic spring-data interfaces providing access to the data by full objects
interface PizzaRepository : CrudRepository<Pizza, Long> {

    // interface based projection, only select the field PizzaDescription has
    //
    // Note: just naming our method findAll() would be correct, however it clashes with the base method.
    //       See https://stackoverflow.com/a/50880684
    //      vvvvvvvvvvvv <- ignored
    fun findDescriptionsBy(): List<PizzaDescription>

    fun findPizzaByIdIn(ids: Set<Long>): List<Pizza> // magic method "SELECT * FROM pizza WHERE ids IN (:ids)"
}

interface PizzaToppingRepository : CrudRepository<PizzaTopping, Long> {

    @Query(
        "SELECT p.name AS pizza_name, t.topping " + // rename column to match POJO's property
        "FROM pizza_topping t " +
        "JOIN pizza p ON p.id = t.pizza_id"
    )
    fun findPizzaNameAndTopping(): List<ToppingResult>
}

interface OrderRepository : CrudRepository<Order, Long> {

    @Query("SELECT id FROM shop_order")
    fun findIds(): Set<Long>

    @Query("SELECT DISTINCT customer_id FROM shop_order WHERE id IN (:ids)")
    fun findDistinctCustomerIdWhereIdIn(ids: Set<Long>): Set<Long>
}

interface OrderItemRepository : CrudRepository<OrderItem, Long> {

    fun findByOrderIdIn(orderIds: Set<Long>): Set<OrderItem> // magic method "SELECT * FROM shop_order_item WHERE shop_order_id IN (:orderIds)"
}

interface CustomerRepository : CrudRepository<Customer, Long> {

    fun findByIdIn(ids: Set<Long>): List<Customer> // magic method "SELECT * FROM customer WHERE id IN (:ids)"
}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val pizzaRepository: PizzaRepository,
    private val customerRepository: CustomerRepository
) {

    fun getOrdersSummary(): List<OrderSummaryResult> {
        val orderIds = orderRepository.findIds()

        val orderItemsByOrderId = orderItemRepository
            .findByOrderIdIn(orderIds)
            .groupBy { it.orderId }

        val pizzasById = orderItemsByOrderId
            .asSequence()
            .flatMap { it.value }
            .map { it.pizzaId }
            .toSet()
            .let { pizzaRepository.findPizzaByIdIn(it) }
            .associateBy { it.id }

        val customersById = orderRepository
            .findDistinctCustomerIdWhereIdIn(orderIds)
            .let { customerRepository.findByIdIn(it) }
            .associateBy { it.id }

        return orderRepository.findAll()
            .map { order ->
                val customer = customersById.getValue(order.customerId)
                val orderItems = orderItemsByOrderId[order.id].orEmpty()

                OrderSummaryResult(
                    order.id,
                    order.date.toLocalDate(),
                    customer.firstName,
                    customer.lastName,
                    orderItems.map { orderItem ->
                        val pizza = pizzasById.getValue(orderItem.pizzaId)

                        OrderSummaryResultItem(
                            PizzaResult(pizza.id, pizza.name),
                            orderItem.amount
                        )
                    }
                )
            }
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
}

@Service
class PizzaService(
    private val pizzaRepository: PizzaRepository,
    private val pizzaToppingRepository: PizzaToppingRepository,
) {

    fun getPizzasFullObject(): List<PizzaResult> = pizzaRepository
        .findAll()
        .map { PizzaResult(it.id, it.name) } // <- like with Hibernate, we are back to working with (full) objects, it = Pizza (with the (unused) toppings)

    // having mapped Pizza.toppings:
    // this will lead to n+1 queries; n = fetching the toppings for each pizza,
    // despite the topping are not requested though the projection interface PizzaDescription
    fun getPizzas(): List<PizzaDescription> = pizzaRepository.findDescriptionsBy()

    fun getToppings(): List<ToppingResult> = pizzaToppingRepository
        .findPizzaNameAndTopping()
}

data class PizzaResult(
    val id: Long,
    val name: String
)

data class ToppingResult(
    val topping: String,
    val pizzaName: String
)

@SpringBootApplication
class SpringDataMain

fun main(args: Array<String>) {
    runApplication<SpringDataMain>(*args)
}

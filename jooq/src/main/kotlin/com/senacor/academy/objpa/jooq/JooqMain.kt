package com.senacor.academy.objpa.jooq

import com.senacor.academy.objpa.jooq.Keys.FK_PIZZA_TOPPING_PIZZA
import com.senacor.academy.objpa.jooq.Keys.FK_SHOP_ORDER_CUSTOMER
import com.senacor.academy.objpa.jooq.Keys.FK_SHOP_ORDER_ITEM_PIZZA
import com.senacor.academy.objpa.jooq.Tables.CUSTOMER
import com.senacor.academy.objpa.jooq.Tables.PIZZA
import com.senacor.academy.objpa.jooq.Tables.PIZZA_TOPPING
import com.senacor.academy.objpa.jooq.Tables.SHOP_ORDER
import com.senacor.academy.objpa.jooq.Tables.SHOP_ORDER_ITEM
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
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

    @GetMapping("/pizzas/toppings")
    fun getToppings() = pizzaService.getToppingsFetch()
}

@Service
class PizzaService(private val create: DSLContext) {

    fun getPizzasWithoutMetaData(): List<PizzaResult> = create
        .select(field("id"), field("name"))
        .from(table("pizza"))
        .fetchInto(PizzaResult::class.java)

    fun getPizzas(): List<PizzaResult> = create
        .select(PIZZA.ID, PIZZA.NAME)
        .from(PIZZA)
        .fetchInto(PizzaResult::class.java)

    fun getToppingsException() = create
        .select(PIZZA_TOPPING.TOPPING, PIZZA.NAME)
        .from(PIZZA_TOPPING)
        .join(PIZZA).onKey(FK_PIZZA_TOPPING_PIZZA)
        .fetchInto(ToppingResult::class.java)

    fun getToppingsFetchInto() = create
        .select(PIZZA_TOPPING.TOPPING, PIZZA.NAME)
        .from(PIZZA_TOPPING)
        .join(PIZZA).onKey(FK_PIZZA_TOPPING_PIZZA)
        .fetchInto(ToppingResult::class.java)

    fun getToppingsFetch() = create
        .select(PIZZA_TOPPING.TOPPING, PIZZA.NAME)
        .from(PIZZA_TOPPING)
        .join(PIZZA).onKey(FK_PIZZA_TOPPING_PIZZA)
        .fetch { ToppingResult(it[PIZZA_TOPPING.TOPPING], it[PIZZA.NAME]) } // <- map yourself

    fun getOrdersSummary(): List<OrderSummaryResult> {
        val orders = create
            .select(
                SHOP_ORDER.ID, SHOP_ORDER.DATE,
                CUSTOMER.FIRST_NAME, CUSTOMER.LAST_NAME
            )
            .from(SHOP_ORDER)
            .join(CUSTOMER).onKey(FK_SHOP_ORDER_CUSTOMER) // naming your CONSTRAINTS makes easy JOINs
            .fetch()

        val orderIds = orders
            .map { it[SHOP_ORDER.ID] } // jOOQ knows which column is where in the result, and what type
            .distinct()

        val itemsWithPizzas = create
            .select(
                SHOP_ORDER_ITEM.SHOP_ORDER_ID, SHOP_ORDER_ITEM.AMOUNT,
                PIZZA.ID, PIZZA.NAME
            )
            .from(SHOP_ORDER_ITEM)
            .join(PIZZA).onKey(FK_SHOP_ORDER_ITEM_PIZZA)
            .where(SHOP_ORDER_ITEM.SHOP_ORDER_ID.`in`(orderIds))
            .fetchGroups(SHOP_ORDER_ITEM.SHOP_ORDER_ID) { // grouping and RecordMapper in one call
                OrderSummaryResultItem(
                    pizza = PizzaResult(it[PIZZA.ID], it[PIZZA.NAME]),
                    amount = it[SHOP_ORDER_ITEM.AMOUNT]
                )
            }

        return orders.map {
            OrderSummaryResult(
                it[SHOP_ORDER.ID],
                it[SHOP_ORDER.DATE],
                it[CUSTOMER.FIRST_NAME],
                it[CUSTOMER.LAST_NAME],
                itemsWithPizzas[it[SHOP_ORDER.ID]].orEmpty()
            )
        }
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

    data class ToppingResult(
        val topping: String,
        val pizzaName: String
    )
}

@SpringBootApplication
class JooqMain

fun main(args: Array<String>) {
    runApplication<JooqMain>(*args)
}

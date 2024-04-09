package com.senacor.academy.objpa.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.spring5.SpringConnectionFactory
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Date
import java.time.LocalDate
import javax.sql.DataSource


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

// Declarative API with SQL Objects
interface PizzaDao {

    @SqlQuery("SELECT id, name FROM pizza")
    fun getPizzas(): List<PizzaResult>
}

// Fluent API with Handle
@Service
class PizzaService(private val jdbi: Jdbi) {

    fun getPizzasManualMapping(): List<PizzaResult> = jdbi.withHandleUnchecked { handle ->
        handle
            .createQuery("SELECT id, name FROM pizza")
            .map { rs, _ ->
                PizzaResult(rs.getLong("id"), rs.getString("name"))
            }
            .list()
    }

    fun getPizzasMapped(): List<PizzaResult> = jdbi.withHandleUnchecked { handle ->
        // for Java with default constructor an POJO:
        // handle.registerRowMapper(BeanMapper.factory(PizzaResult::class.java))

        // for Kotlin:
        // handle.registerRowMapper(KotlinMapper(PizzaResult::class))

        // Since we have the Kotlin plugin enabled, this registration is automatically done.

        handle
            .createQuery("SELECT id, name FROM pizza")
            .mapTo(PizzaResult::class.java)
            .list()
    }

    fun getPizzasMapTo(): List<PizzaResult> = jdbi.withHandleUnchecked { handle ->
        handle
            .createQuery("SELECT id, name FROM pizza")
            .mapTo(PizzaResult::class.java)
            .list()
    }

    fun getPizzas(): List<PizzaResult> = jdbi.withHandleUnchecked { handle ->
        val pizzaDao = handle.attach(PizzaDao::class.java) // get an implementation of our interface

        pizzaDao.getPizzas()
    }

    fun getToppingsFetch(): List<ToppingResult> = jdbi.withHandleUnchecked { handle ->
        handle
            .createQuery(
                "SELECT pt.topping, p.name AS pizzaName " + // <- rename column, so it matches the POJO
                "FROM pizza_topping pt " +
                "JOIN pizza p ON pt.pizza_id = p.id"
            )
            .mapTo(ToppingResult::class.java)
            .list()
    }

    fun getOrdersSummary(): List<OrderSummaryResult> = jdbi.withHandleUnchecked { handle ->
        val orders = handle
            .createQuery(
                "SELECT " +
                "  shop_order.id, shop_order.date, " +
                "  customer.first_name, customer.last_name " +
                "FROM shop_order " +
                "JOIN customer ON customer.id = shop_order.customer_id"
            )
            .mapToMap() // we choose an untyped "Map<String, Any>"
            .list()

        val orderIds = orders
            .map { it["id"] as Long } // Since there is no type information, we need to assist Kotlin
            .distinct()

        val itemsWithPizzas = handle
            .createQuery(
                "SELECT " +
                "  shop_order_item.shop_order_id, shop_order_item.amount, " +
                "  pizza.id AS pizza_id, pizza.name AS pizza_name " +
                "FROM shop_order_item " +
                "JOIN pizza ON pizza.id = shop_order_item.pizza_id " +
                "WHERE shop_order_item.shop_order_id in (<orderIds>)" // <- bind list
            )
            .bindList("orderIds", orderIds)
            .mapToMap()
            .list()
            .groupBy(
                { it["shop_order_id"] as Long },
                {
                    OrderSummaryResultItem(
                        pizza = PizzaResult(it["pizza_id"] as Long, it["pizza_name"] as String),
                        amount = it["amount"] as Int
                    )
                }
            )

        orders.map {
            OrderSummaryResult(
                it["id"] as Long,
                (it["date"] as Date).toLocalDate(), // we get SQL type java.sql.Date from JDBC/Jdbi
                it["first_name"] as String,
                it["last_name"] as String,
                itemsWithPizzas[it["id"] as Long].orEmpty()
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

    data class ToppingResult(
        val topping: String,
        val pizzaName: String
    )
}

data class PizzaResult(
    val id: Long,
    val name: String
)

@SpringBootApplication
class JdbiMain {

    @Bean
    fun jdbi(dataSource: DataSource): Jdbi {
        val connectionFactory = SpringConnectionFactory(dataSource)

        return Jdbi.create(connectionFactory).apply {
            installPlugin(KotlinPlugin()) // registers KotlinMapper
            installPlugin(SqlObjectPlugin()) // allows to use attach()+DaoInterface
        }
    }
}

fun main(args: Array<String>) {
    runApplication<JdbiMain>(*args)
}

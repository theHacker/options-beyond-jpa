package com.senacor.academy.objpa.springjdbc

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Service
import java.sql.Date

@Qualifier("jdbcClient")
@Service
class JdbcClientPizzaService(private val jdbcClient: JdbcClient) : PizzaService {

    fun getPizzasManualMapping(): List<PizzaResult> = jdbcClient
        .sql("SELECT id, name FROM pizza")
        .query { rs, _ ->
            PizzaResult(
                rs.getLong("id"),
                rs.getString("name")
            )
        }
        .list()

    override fun getPizzas(): List<PizzaResultJavaStyle> = jdbcClient
        .sql("SELECT id, name FROM pizza")
        .query(BeanPropertyRowMapper(PizzaResultJavaStyle::class.java)) // needs a no-arg constructor
        .list()

    override fun getToppings(): List<ToppingResult> = jdbcClient
        .sql(
            "SELECT pt.topping, p.name " +
            "FROM pizza_topping pt " +
            "JOIN pizza p ON pt.pizza_id = p.id"
        )
        .query { rs, _ ->
            ToppingResult(
                rs.getString("topping"),
                rs.getString("name")
            )
        }
        .list()

    override fun getOrdersSummary(): List<OrderSummaryResult> {
        val orders = jdbcClient
            .sql(
                "SELECT " +
                "  shop_order.id, shop_order.date, " +
                "  customer.first_name, customer.last_name " +
                "FROM shop_order " +
                "JOIN customer ON customer.id = shop_order.customer_id"
            )
            .query()
            .listOfRows() // returns untyped "List<Map<String, Any>>"

        // Note: param*() functions still have the limits of the JdbcTemplate,
        // not able to bind "?" to multiple values for an IN clause.

        val orderIds = orders
            .map { it["id"] as Long } // Since there is no type information, we need to assist Kotlin
            .distinct()
            .also {
                assert(it.isNotEmpty()) // should not be empty
                assert(it.size < 1000) // too many values will likely get an error because the query will be too long
            }
            .joinToString(",") // prepare to be used in a SQL statement

        val itemsWithPizzas = jdbcClient
            .sql(
                "SELECT " +
                "  shop_order_item.shop_order_id, shop_order_item.amount, " +
                "  pizza.id AS pizza_id, pizza.name AS pizza_name " +
                "FROM shop_order_item " +
                "JOIN pizza ON pizza.id = shop_order_item.pizza_id " +
                "WHERE shop_order_item.shop_order_id in ($orderIds)", // <- only numeric values from the database, this is safe
            )
            .query()
            .listOfRows()
            .groupBy(
                { it["shop_order_id"] as Long },
                {
                    OrderSummaryResultItem(
                        pizza = PizzaResult(it["pizza_id"] as Long, it["pizza_name"] as String),
                        amount = it["amount"] as Int
                    )
                }
            )

        return orders.map {
            OrderSummaryResult(
                it["id"] as Long,
                (it["date"] as Date).toLocalDate(), // we get SQL type java.sql.Date from JDBC
                it["first_name"] as String,
                it["last_name"] as String,
                itemsWithPizzas[it["id"] as Long].orEmpty()
            )
        }
    }
}

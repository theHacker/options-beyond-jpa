package com.senacor.academy.objpa.springjdbc

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.Date

@Qualifier("jdbcTemplate")
@Service
class JdbcTemplatePizzaService(private val jdbcTemplate: JdbcTemplate) : PizzaService {

    fun getPizzasManualMapping(): List<PizzaResult> = jdbcTemplate
        .query("SELECT id, name FROM pizza") { rs, _ ->
            PizzaResult(
                rs.getLong("id"),
                rs.getString("name")
            )
        }

    override fun getPizzas(): List<PizzaResultJavaStyle> = jdbcTemplate
        .query(
            "SELECT id, name FROM pizza",
            BeanPropertyRowMapper(PizzaResultJavaStyle::class.java) // needs a no-arg constructor
        )

    override fun getToppings(): List<ToppingResult> = jdbcTemplate
        .query(
            "SELECT pt.topping, p.name " +
            "FROM pizza_topping pt " +
            "JOIN pizza p ON pt.pizza_id = p.id"
        ) { rs, _ ->
            ToppingResult(
                rs.getString("topping"),
                rs.getString("name")
            )
        }

    override fun getOrdersSummary(): List<OrderSummaryResult> {
        val orders = jdbcTemplate.queryForList( // returns untyped "List<Map<String, Any>>"
            "SELECT " +
            "  shop_order.id, shop_order.date, " +
            "  customer.first_name, customer.last_name " +
            "FROM shop_order " +
            "JOIN customer ON customer.id = shop_order.customer_id"
        )

        // Note: "?" in a query only bind exactly one value, for an IN clause
        // we need to do nasty stuff here.

        val orderIds = orders
            .map { it["id"] as Long } // Since there is no type information, we need to assist Kotlin
            .distinct()
            .also {
                assert(it.isNotEmpty()) // should not be empty
                assert(it.size < 1000) // too many values will likely get an error because the query will be too long
            }
            .joinToString(",") // prepare to be used in a SQL statement

        val itemsWithPizzas = jdbcTemplate
            .queryForList(
                "SELECT " +
                "  shop_order_item.shop_order_id, shop_order_item.amount, " +
                "  pizza.id AS pizza_id, pizza.name AS pizza_name " +
                "FROM shop_order_item " +
                "JOIN pizza ON pizza.id = shop_order_item.pizza_id " +
                "WHERE shop_order_item.shop_order_id in ($orderIds)", // <- only numeric values from the database, this is safe
            )
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

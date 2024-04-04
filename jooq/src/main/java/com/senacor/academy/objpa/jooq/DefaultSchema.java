/*
 * This file is generated by jOOQ.
 */
package com.senacor.academy.objpa.jooq;


import com.senacor.academy.objpa.jooq.tables.Customer;
import com.senacor.academy.objpa.jooq.tables.Pizza;
import com.senacor.academy.objpa.jooq.tables.PizzaTopping;
import com.senacor.academy.objpa.jooq.tables.ShopOrder;
import com.senacor.academy.objpa.jooq.tables.ShopOrderItem;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>CUSTOMER</code>.
     */
    public final Customer CUSTOMER = Customer.CUSTOMER;

    /**
     * The table <code>PIZZA</code>.
     */
    public final Pizza PIZZA = Pizza.PIZZA;

    /**
     * The table <code>PIZZA_TOPPING</code>.
     */
    public final PizzaTopping PIZZA_TOPPING = PizzaTopping.PIZZA_TOPPING;

    /**
     * The table <code>SHOP_ORDER</code>.
     */
    public final ShopOrder SHOP_ORDER = ShopOrder.SHOP_ORDER;

    /**
     * The table <code>SHOP_ORDER_ITEM</code>.
     */
    public final ShopOrderItem SHOP_ORDER_ITEM = ShopOrderItem.SHOP_ORDER_ITEM;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Customer.CUSTOMER,
            Pizza.PIZZA,
            PizzaTopping.PIZZA_TOPPING,
            ShopOrder.SHOP_ORDER,
            ShopOrderItem.SHOP_ORDER_ITEM
        );
    }
}

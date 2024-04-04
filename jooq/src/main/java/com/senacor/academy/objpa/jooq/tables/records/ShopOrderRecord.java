/*
 * This file is generated by jOOQ.
 */
package com.senacor.academy.objpa.jooq.tables.records;


import com.senacor.academy.objpa.jooq.tables.ShopOrder;

import java.time.LocalDate;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ShopOrderRecord extends UpdatableRecordImpl<ShopOrderRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>SHOP_ORDER.ID</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>SHOP_ORDER.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>SHOP_ORDER.CUSTOMER_ID</code>.
     */
    public void setCustomerId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>SHOP_ORDER.CUSTOMER_ID</code>.
     */
    public Long getCustomerId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>SHOP_ORDER.DATE</code>.
     */
    public void setDate(LocalDate value) {
        set(2, value);
    }

    /**
     * Getter for <code>SHOP_ORDER.DATE</code>.
     */
    public LocalDate getDate() {
        return (LocalDate) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ShopOrderRecord
     */
    public ShopOrderRecord() {
        super(ShopOrder.SHOP_ORDER);
    }

    /**
     * Create a detached, initialised ShopOrderRecord
     */
    public ShopOrderRecord(Long id, Long customerId, LocalDate date) {
        super(ShopOrder.SHOP_ORDER);

        setId(id);
        setCustomerId(customerId);
        setDate(date);
        resetChangedOnNotNull();
    }
}
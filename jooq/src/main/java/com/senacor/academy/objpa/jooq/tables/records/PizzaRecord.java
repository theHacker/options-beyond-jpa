/*
 * This file is generated by jOOQ.
 */
package com.senacor.academy.objpa.jooq.tables.records;


import com.senacor.academy.objpa.jooq.tables.Pizza;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PizzaRecord extends UpdatableRecordImpl<PizzaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>PIZZA.ID</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>PIZZA.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>PIZZA.NAME</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>PIZZA.NAME</code>.
     */
    public String getName() {
        return (String) get(1);
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
     * Create a detached PizzaRecord
     */
    public PizzaRecord() {
        super(Pizza.PIZZA);
    }

    /**
     * Create a detached, initialised PizzaRecord
     */
    public PizzaRecord(Long id, String name) {
        super(Pizza.PIZZA);

        setId(id);
        setName(name);
        resetChangedOnNotNull();
    }
}

package com.senacor.academy.objpa.hibernate

import jakarta.persistence.Basic
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Pizza(
    @Id
    val id: Long,

    @Basic
    val name: String,

    @ElementCollection
    val toppings: Set<String>
) {

    override fun toString(): String {
        return "Pizza $name with ${toppings.joinToString(", ")}"
    }
}

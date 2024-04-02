package com.senacor.academy.objpa.hibernate

import jakarta.persistence.Basic
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity
class Customer(
    @Id
    val id: Long,

    @Basic
    var firstName: String,
    @Basic
    var lastName: String
)

@Entity
class ShopOrder(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long?,

    @ManyToOne
    val customer: Customer,

    @Basic
    val date: LocalDate,

    @OneToMany(mappedBy = "shopOrder")
    val items: List<ShopOrderItem>
)

@Entity
class ShopOrderItem(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long?,

    @ManyToOne
    val shopOrder: ShopOrder,
    @ManyToOne
    val pizza: Pizza,

    @Basic
    val amount: Int
)

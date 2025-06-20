package com.bookstore.usermanagement.model

import jakarta.persistence.*

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["user_name"])])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_name", nullable = false, unique = true)
    val userName: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val fullName: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val role: String = "USERS"
)

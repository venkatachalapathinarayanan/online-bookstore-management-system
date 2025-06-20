package com.bookstore.usermanagement.model

import com.bookstore.usermanagement.model.User

class UserBuilder {
    private var userName: String? = null
    private var fullName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var phoneNumber: String? = null
    private var address: String? = null
    private var role: String? = null

    fun userName(userName: String) = apply { this.userName = userName }
    fun fullName(fullName: String) = apply { this.fullName = fullName }
    fun email(email: String) = apply { this.email = email }
    fun password(password: String) = apply { this.password = password }
    fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
    fun address(address: String) = apply { this.address = address }
    fun role(role: String) = apply { this.role = role }

    fun build(): User {
        return User(
            userName = userName ?: throw IllegalArgumentException("Username is required"),
            fullName = fullName ?: throw IllegalArgumentException("Full name is required"),
            email = email ?: throw IllegalArgumentException("Email is required"),
            password = password ?: throw IllegalArgumentException("Password is required"),
            phoneNumber = phoneNumber ?: throw IllegalArgumentException("Phone Number is required"),
            address = address ?: throw IllegalArgumentException("address is required"),
            role = role ?: throw IllegalArgumentException("Role is required")
        )
    }
}


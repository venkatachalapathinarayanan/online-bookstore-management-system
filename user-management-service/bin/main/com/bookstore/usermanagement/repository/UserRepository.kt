package com.bookstore.usermanagement.repository

import com.bookstore.usermanagement.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(userName: String): Optional<User>
    fun existsByUserName(userName: String): Boolean
}


package com.bookstore.usermanagement.config

import com.bookstore.usermanagement.model.User
import com.bookstore.usermanagement.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)
        val user: User = userRepository.findByUserName(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
        logger.info("User retrieved from DB: {} with role {}", user.userName, user.role)
        val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role))
        return org.springframework.security.core.userdetails.User(
            user.userName,
            user.password,
            authorities
        )
    }
}


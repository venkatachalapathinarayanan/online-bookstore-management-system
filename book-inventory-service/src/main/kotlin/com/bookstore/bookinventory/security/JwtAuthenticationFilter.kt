package com.bookstore.bookinventory.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        val token = header.substring(7)
        try {
            val username = jwtUtil.extractUsername(token)
            val roles = jwtUtil.extractRoles(token)
            if (username.isNotBlank() && SecurityContextHolder.getContext().authentication == null) {
                if (!jwtUtil.validateToken(token)) {
                    throw Exception("Invalid JWT token")
                }
                val authorities = roles.map { SimpleGrantedAuthority(it) }
                val auth = UsernamePasswordAuthenticationToken(username, null, authorities)
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: Exception) {
            logger.warn("JWT error: ${'$'}{ex.message}")
        }
        filterChain.doFilter(request, response)
    }
}


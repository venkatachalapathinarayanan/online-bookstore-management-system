package com.bookstore.ordermanagement.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {
    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)
    fun extractRoles(token: String): List<String> =
        extractAllClaims(token)["roles"] as? List<String> ?: emptyList()
    fun validateToken(token: String): Boolean = !isTokenExpired(token)
    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }
    private fun extractAllClaims(token: String): Claims =
        Jwts.parser().setSigningKey(secret.toByteArray()).parseClaimsJws(token).body
    private fun isTokenExpired(token: String): Boolean =
        extractAllClaims(token).expiration.before(Date())
}


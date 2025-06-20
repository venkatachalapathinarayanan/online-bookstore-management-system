package com.bookstore.usermanagement.security

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
    fun generateToken(username: String, roles: List<String>): String {
        val claims = HashMap<String, Any>()
        claims["roles"] = roles
        return createToken(claims, username)
    }

    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)

    fun extractRoles(token: String): List<String> =
        extractAllClaims(token)["roles"] as? List<String> ?: emptyList()

    fun validateToken(token: String, username: String): Boolean {
        val extractedUsername = extractUsername(token)
        return (extractedUsername == username && !isTokenExpired(token))
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Date()
        val expiry = Date(now.time + expiration * 1000)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, secret.toByteArray())
            .compact()
    }

    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser().setSigningKey(secret.toByteArray()).parseClaimsJws(token).body

    private fun isTokenExpired(token: String): Boolean =
        extractAllClaims(token).expiration.before(Date())
}


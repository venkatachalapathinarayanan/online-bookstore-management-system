package com.bookstore.ordermanagement.service

import com.bookstore.ordermanagement.dto.BookPriceRequest
import com.bookstore.ordermanagement.dto.BookPriceResponse
import com.bookstore.ordermanagement.dto.BookResponse
import com.bookstore.ordermanagement.security.JwtUtil
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration

@Service
class BookInventoryClient(
    private val webClient: WebClient,
    private val jwtUtil: JwtUtil
) {
    private val logger = LoggerFactory.getLogger(BookInventoryClient::class.java)
    
    @CircuitBreaker(name = "bookInventoryService", fallbackMethod = "getBookPriceFallback")
    fun getBookPrice(bookId: Long): Mono<BigDecimal> {
        logger.debug("Fetching price for book ID: {}", bookId)
        
        return webClient.get()
            .uri("/api/books/{bookId}", bookId)
            .header("Authorization", "Bearer ${generateServiceToken()}")
            .retrieve()
            .bodyToMono(BookResponse::class.java)
            .map { it.price }
            .timeout(Duration.ofSeconds(5))
            .doOnError { error ->
                logger.error("Error fetching price for book {}: {}", bookId, error.message)
            }
    }
    
    @CircuitBreaker(name = "bookInventoryService", fallbackMethod = "getBookPricesFallback")
    fun getBookPrices(bookIds: List<Long>): Mono<Map<Long, BigDecimal>> {
        if (bookIds.isEmpty()) {
            return Mono.just(emptyMap())
        }
        
        logger.debug("Fetching prices for {} books", bookIds.size)
        
        return webClient.post()
            .uri("/api/books/prices")
            .header("Authorization", "Bearer ${generateServiceToken()}")
            .bodyValue(BookPriceRequest(bookIds))
            .retrieve()
            .bodyToMono(BookPriceResponse::class.java)
            .map { it.prices }
            .timeout(Duration.ofSeconds(10))
            .doOnError { error ->
                logger.error("Error fetching prices for books {}: {}", bookIds, error.message)
            }
    }
    
    // Fallback methods for circuit breaker
    fun getBookPriceFallback(bookId: Long, exception: Exception): Mono<BigDecimal> {
        logger.warn("Using fallback for book price {}: {}", bookId, exception.message)
        return Mono.just(BigDecimal.ZERO)
    }
    
    fun getBookPricesFallback(bookIds: List<Long>, exception: Exception): Mono<Map<Long, BigDecimal>> {
        logger.warn("Using fallback for book prices {}: {}", bookIds, exception.message)
        return Mono.just(bookIds.associateWith { BigDecimal.ZERO })
    }
    
    private fun generateServiceToken(): String {
        // Generate a simple service token for inter-service communication
        // In production, you might want to use a more sophisticated approach
        return jwtUtil.generateServiceToken("order-management-service", listOf("ADMIN"))
    }
} 
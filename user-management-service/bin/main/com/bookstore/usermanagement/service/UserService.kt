package com.bookstore.usermanagement.service

import com.bookstore.common.event.EventMessage
import com.bookstore.common.kafka.KafkaEventPublisher
import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.model.User
import com.bookstore.usermanagement.model.UserBuilder
import com.bookstore.usermanagement.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import java.util.NoSuchElementException


@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val kafkaEventPublisher: KafkaEventPublisher // Inject publisher
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @PreAuthorize("permitAll()")
    fun addUser(request: UserRequestDTO): UserResponseDTO {
        logger.info("Request to add user: {}", request.userName)
        if (userRepository.existsByUserName(request.userName)) {
            logger.warn("Username already exists: {}", request.userName)
            throw IllegalArgumentException("Username already exists")
        }
        val user = UserBuilder()
            .userName(request.userName)
            .fullName(request.fullName)
            .email(request.email)
            .password(passwordEncoder.encode(request.password))
            .phoneNumber(request.phoneNumber)
            .address(request.address)
            .role("USERS")
            .build()
        val saved = userRepository.save(user)
        logger.info("User created successfully: {}", saved.userName)
        // Publish user created event
        val event = EventMessage(
            eventType = "UserCreated",
            payload = mapOf(
                "userId" to saved.id,
                "userName" to saved.userName,
                "email" to saved.email
            )
        )
        kafkaEventPublisher.publish("user-events", event)
        return saved.toResponseDTO()
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN') or #id == authentication.principal.id")
    fun getUser(id: Long): UserResponseDTO {
        logger.info("Fetching user with id: {}", id)
        return userRepository.findById(id).map { it.toResponseDTO() }
            .orElseThrow {
                logger.warn("User not found for id: {}", id)
                NoSuchElementException("User not found")
            }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    fun listUsers(): List<UserResponseDTO> {
        logger.info("Listing all users")
        return userRepository.findAll().map { it.toResponseDTO() }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN') or #id == authentication.principal.id")
    @Transactional
    fun updateUser(id: Long, request: UserRequestDTO): UserResponseDTO {
        try {
            logger.info("Updating user with id: {}", id)
            val user = userRepository.findById(id).orElseThrow {
                logger.warn("User not found for id: {}", id)
                ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            }
            if (user.userName != request.userName && userRepository.existsByUserName(request.userName)) {
                logger.warn("Username already exists: {}", request.userName)
                throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
            }
            val updated = user.copy(
                userName = request.userName,
                fullName = request.fullName,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                phoneNumber = request.phoneNumber,
                address = request.address,
                role = "USERS"
            )
            logger.info("User updated successfully: {}", updated.userName)
            return userRepository.save(updated).toResponseDTO()
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error updating user with id: {}", id, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN') or #id == authentication.principal.id")
    fun deleteUser(id: Long) {
        try {
            logger.info("Deleting user with id: {}", id)
            if (!userRepository.existsById(id)) {
                logger.warn("User not found for id: {}", id)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            }
            userRepository.deleteById(id)
            logger.info("User deleted successfully: {}", id)
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error deleting user with id: {}", id, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    fun getAllUsers(): List<String> {
        return userRepository.findAll().map { it.userName }
    }

    private fun User.toResponseDTO(): UserResponseDTO = UserResponseDTO(
        id = this.id,
        userName = this.userName,
        fullName = this.fullName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        address = this.address,
        role = this.role
    )
}

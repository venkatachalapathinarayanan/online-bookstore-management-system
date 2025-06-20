package com.bookstore.usermanagement.service

import com.bookstore.usermanagement.dto.UserRequestDTO
import com.bookstore.usermanagement.dto.UserResponseDTO
import com.bookstore.usermanagement.model.User
import com.bookstore.usermanagement.model.UserBuilder
import com.bookstore.usermanagement.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import java.util.NoSuchElementException
import org.slf4j.LoggerFactory
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@Service
class AdminUserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    private val logger = LoggerFactory.getLogger(AdminUserService::class.java)

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    fun addAdmin(request: UserRequestDTO): UserResponseDTO {
        logger.info("Request to add admin user: {}", request.userName)
        try {
            if (userRepository.existsByUserName(request.userName)) {
                logger.warn("Username already exists: {}", request.userName)
                throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
            }
            val user = UserBuilder()
                .userName(request.userName)
                .fullName(request.fullName)
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .phoneNumber(request.phoneNumber)
                .address(request.address)
                .role("ADMIN")
                .build()
            val saved = userRepository.save(user)
            logger.info("Admin user created successfully: {}", saved.userName)
            return saved.toResponseDTO()
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error creating admin user: {}", request.userName, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    @PreAuthorize("hasAuthority('SUPERADMIN') or #id == authentication.principal.id")
    fun getAdmin(id: Long): UserResponseDTO {
        logger.info("Fetching admin user with id: {}", id)
        return try {
            userRepository.findById(id)
                .filter { it.role == "ADMIN" }
                .map { it.toResponseDTO() }
                .orElseThrow {
                    logger.warn("Admin user not found for id: {}", id)
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found")
                }
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error getting admin user with id: {}", id, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    fun listAdmins(): List<UserResponseDTO> {
        logger.info("Listing all admin users")
        return try {
            userRepository.findAll().filter { it.role == "ADMIN" }.map { it.toResponseDTO() }
        } catch (ex: Exception) {
            logger.error("Unexpected error listing admin users", ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    @PreAuthorize("hasAuthority('SUPERADMIN') or #id == authentication.principal.id")
    @Transactional
    fun updateAdmin(id: Long, request: UserRequestDTO): UserResponseDTO {
        logger.info("Request to update admin user with id: {}", id)
        try {
            val user = userRepository.findById(id).orElseThrow {
                logger.warn("Admin user not found for id: {}", id)
                ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found")
            }
            if (user.role != "ADMIN") {
                logger.warn("Not an admin user for id: {}", id)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not an admin user")
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
                address = request.address
            )
            logger.info("Updating admin user with id: {}", id)
            return userRepository.save(updated).toResponseDTO()
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error updating admin user with id: {}", id, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
    }

    @PreAuthorize("hasAuthority('SUPERADMIN') or #id == authentication.principal.id")
    fun deleteAdmin(id: Long) {
        logger.info("Request to delete admin user with id: {}", id)
        try {
            val user = userRepository.findById(id).orElseThrow {
                logger.warn("Admin user not found for id: {}", id)
                ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found")
            }
            if (user.role != "ADMIN") {
                logger.warn("Not an admin user for id: {}", id)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not an admin user")
            }
            logger.info("Deleting admin user with id: {}", id)
            userRepository.deleteById(id)
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error deleting admin user with id: {}", id, ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
        }
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

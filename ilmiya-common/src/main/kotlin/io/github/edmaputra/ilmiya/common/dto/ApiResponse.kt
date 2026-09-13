package io.github.edmaputra.ilmiya.common.dto

import java.time.Instant

/**
 * Standard REST API response envelope.
 *
 * @param T payload data type
 * @author edmaputra
 * @since 0.0.1
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: List<String>? = null,
    val timestamp: Instant = Instant.now()
) {
    companion object {
        fun <T> ok(data: T, message: String = "Operation completed successfully"): ApiResponse<T> =
            ApiResponse(success = true, message = message, data = data)

        fun <T> ok(data: T): ApiResponse<T> = ok(data, "Operation completed successfully")

        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> =
            ApiResponse(success = false, message = message, data = null, errors = errors)

        fun <T> error(message: String): ApiResponse<T> = error(message, null)
    }
}

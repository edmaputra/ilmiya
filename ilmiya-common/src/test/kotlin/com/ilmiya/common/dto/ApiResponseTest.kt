package com.ilmiya.common.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApiResponseTest {

    @Test
    fun `should create successful response with default message`() {
        val response = ApiResponse.ok("test-data")

        assertThat(response.success).isTrue()
        assertThat(response.data).isEqualTo("test-data")
        assertThat(response.message).isEqualTo("Operation completed successfully")
        assertThat(response.errors).isNull()
    }

    @Test
    fun `should create error response`() {
        val response = ApiResponse.error<String>("Failed", listOf("Error 1", "Error 2"))

        assertThat(response.success).isFalse()
        assertThat(response.message).isEqualTo("Failed")
        assertThat(response.data).isNull()
        assertThat(response.errors).containsExactly("Error 1", "Error 2")
    }
}

package io.github.edmaputra.ilmiya.common.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PageResponseTest {

    @Test
    fun `should create first page with correct totalPages and bounds`() {
        val items = listOf("A", "B", "C")
        val response = PageResponse.of(
            content = items,
            page = 0,
            size = 3,
            totalElements = 10
        )

        assertThat(response.content).containsExactly("A", "B", "C")
        assertThat(response.page).isEqualTo(0)
        assertThat(response.size).isEqualTo(3)
        assertThat(response.totalElements).isEqualTo(10)
        assertThat(response.totalPages).isEqualTo(4)
        assertThat(response.first).isTrue()
        assertThat(response.last).isFalse()
    }

    @Test
    fun `should correctly mark last page`() {
        val response = PageResponse.of(
            content = listOf("J"),
            page = 3,
            size = 3,
            totalElements = 10
        )

        assertThat(response.first).isFalse()
        assertThat(response.last).isTrue()
    }

    @Test
    fun `should handle zero size without division by zero`() {
        val response = PageResponse.of(
            content = emptyList<String>(),
            page = 0,
            size = 0,
            totalElements = 0
        )

        assertThat(response.totalPages).isEqualTo(1)
        assertThat(response.first).isTrue()
        assertThat(response.last).isTrue()
    }
}

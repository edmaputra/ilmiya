package io.github.edmaputra.ilmiya.core.tenant.adapter.persistence

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.tenant.domain.Tenant
import io.github.edmaputra.ilmiya.core.tenant.domain.TenantStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.Optional

class TenantRepositoryAdapterTest {

    private val springDataRepository = mockk<SpringDataTenantRepository>()
    private val adapter = TenantRepositoryAdapter(springDataRepository)

    private val tenantId = TenantId.of("yayasan-01")

    private val sampleDomainTenant = Tenant(
        id = tenantId,
        name = "Yayasan Pendidikan Al-Azhar",
        legalEntityNumber = "SK-AHU-001",
        registrationType = "YAYASAN",
        address = "Jl. Sisingamangaraja",
        contactEmail = "admin@alazhar.org",
        contactPhone = "021-123456",
        logoUrl = "https://example.com/logo.png",
        status = TenantStatus.ACTIVE,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private val sampleJpaEntity = TenantJpaEntity(
        id = tenantId.value,
        name = "Yayasan Pendidikan Al-Azhar",
        legalEntityNumber = "SK-AHU-001",
        registrationType = "YAYASAN",
        address = "Jl. Sisingamangaraja",
        contactEmail = "admin@alazhar.org",
        contactPhone = "021-123456",
        logoUrl = "https://example.com/logo.png",
        status = "ACTIVE",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `save when tenant does not exist should create new entity and save`() {
        val entitySlot = slot<TenantJpaEntity>()
        every { springDataRepository.findById(tenantId.value) } returns Optional.empty()
        every { springDataRepository.save(capture(entitySlot)) } returns sampleJpaEntity

        val result = adapter.save(sampleDomainTenant)

        assertThat(entitySlot.isCaptured).isTrue()
        val captured = entitySlot.captured
        assertThat(captured.id).isEqualTo(tenantId.value)
        assertThat(captured.name).isEqualTo("Yayasan Pendidikan Al-Azhar")
        assertThat(result.id).isEqualTo(tenantId)
        verify(exactly = 1) { springDataRepository.save(any()) }
    }

    @Test
    fun `save when tenant exists should update existing entity and save`() {
        val existingEntity = TenantJpaEntity(
            id = tenantId.value,
            name = "Old Name",
            address = "Old Address",
            contactEmail = "old@alazhar.org",
            contactPhone = "000"
        )
        every { springDataRepository.findById(tenantId.value) } returns Optional.of(existingEntity)
        every { springDataRepository.save(existingEntity) } returns existingEntity

        val result = adapter.save(sampleDomainTenant)

        assertThat(existingEntity.name).isEqualTo("Yayasan Pendidikan Al-Azhar")
        assertThat(existingEntity.address).isEqualTo("Jl. Sisingamangaraja")
        assertThat(result.name).isEqualTo("Yayasan Pendidikan Al-Azhar")
        verify(exactly = 1) { springDataRepository.save(existingEntity) }
    }

    @Test
    fun `findById should return domain tenant when found`() {
        every { springDataRepository.findById(tenantId.value) } returns Optional.of(sampleJpaEntity)

        val result = adapter.findById(tenantId)

        assertThat(result).isNotNull
        assertThat(result!!.id).isEqualTo(tenantId)
        assertThat(result.name).isEqualTo("Yayasan Pendidikan Al-Azhar")
    }

    @Test
    fun `findById should return null when not found`() {
        every { springDataRepository.findById(tenantId.value) } returns Optional.empty()

        val result = adapter.findById(tenantId)

        assertThat(result).isNull()
    }

    @Test
    fun `existsById should delegate to springDataRepository`() {
        every { springDataRepository.existsById(tenantId.value) } returns true

        assertThat(adapter.existsById(tenantId)).isTrue()
    }

    @Test
    fun `findAll should return paginated tenants`() {
        val page = PageImpl(listOf(sampleJpaEntity))
        every { springDataRepository.findAll(any<PageRequest>()) } returns page

        val result = adapter.findAll(0, 10)

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Yayasan Pendidikan Al-Azhar")
    }

    @Test
    fun `count should delegate to springDataRepository`() {
        every { springDataRepository.count() } returns 5L

        assertThat(adapter.count()).isEqualTo(5L)
    }

    @Test
    fun `deleteById should delegate to springDataRepository`() {
        every { springDataRepository.deleteById(tenantId.value) } returns Unit

        adapter.deleteById(tenantId)

        verify(exactly = 1) { springDataRepository.deleteById(tenantId.value) }
    }
}

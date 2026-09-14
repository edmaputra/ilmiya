package io.github.edmaputra.ilmiya.core.school.application

import io.github.edmaputra.ilmiya.common.domain.TenantId
import io.github.edmaputra.ilmiya.core.school.domain.School
import io.github.edmaputra.ilmiya.core.school.domain.exception.DuplicateInstitutionCodeException
import io.github.edmaputra.ilmiya.core.school.domain.port.`in`.RegisterSchoolCommand
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolEventPublisher
import io.github.edmaputra.ilmiya.core.school.domain.port.out.SchoolRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class RegisterSchoolServiceTest {

    private val repository = mockk<SchoolRepository>()
    private val eventPublisher = mockk<SchoolEventPublisher>(relaxed = true)
    private val service = RegisterSchoolService(repository, eventPublisher)

    @Test
    fun `should successfully register school when valid and code is unique`() {
        val command = RegisterSchoolCommand(
            tenantId = "tenant-sch-01",
            name = "SMP Negeri 1",
            institutionCode = "SMPN1",
            address = "Jl. Merdeka No. 1",
            contactEmail = "admin@smpn1.sch.id",
            contactPhone = "021-123456"
        )

        every { repository.existsByInstitutionCode("SMPN1") } returns false
        every { repository.existsByTenantId(TenantId.of("tenant-sch-01")) } returns false
        every { repository.save(any<School>()) } answers { firstArg() }

        val result = service.register(command)

        assertThat(result.institutionCode).isEqualTo("SMPN1")
        assertThat(result.name).isEqualTo("SMP Negeri 1")
        verify(exactly = 1) { repository.save(any<School>()) }
        verify(atLeast = 1) { eventPublisher.publishAll(any()) }
    }

    @Test
    fun `should throw DuplicateInstitutionCodeException when code already exists`() {
        val command = RegisterSchoolCommand(
            tenantId = "tenant-sch-02",
            name = "SMP Negeri 2",
            institutionCode = "SMPN1",
            address = "Jl. Merdeka No. 2",
            contactEmail = "admin@smpn2.sch.id",
            contactPhone = "021-123457"
        )

        every { repository.existsByInstitutionCode("SMPN1") } returns true

        assertThatThrownBy { service.register(command) }
            .isInstanceOf(DuplicateInstitutionCodeException::class.java)
            .hasMessageContaining("institution code 'SMPN1' already exists")

        verify(exactly = 0) { repository.save(any()) }
    }
}

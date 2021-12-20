package co.uk.collcomputing.application.impl

import co.uk.collcomputing.application.exceptions.AuthorisationException
import co.uk.collcomputing.application.exceptions.DuplicateMerchantException
import co.uk.collcomputing.application.exceptions.MerchantNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MerchantServiceInMemoryImplTest {

    private lateinit var merchantService: MerchantServiceInMemoryImpl

    @BeforeEach
    fun beforeEach() {
        merchantService = MerchantServiceInMemoryImpl()
    }

    // AC: The merchant must be able to register with a given name
    @Test
    fun `successfully register a new merchant`() {
        assertEquals(0, merchantService.count())
        merchantService.register("Nike")
        assertEquals(1, merchantService.count())
    }

    // AC: The name must be guaranteed to be unique across all merchants.
    @Test
    fun `attempt to register with existing merchant throws exception`() {
        merchantService.register("Nike")
        val exception = assertThrows<DuplicateMerchantException> {
            merchantService.register("Nike")
        }
        assertEquals("Merchant is already registered: Nike", exception.message)
    }

    // AC: The merchant must be able to login with a given name
    @Test
    fun `successfully login registered merchant and obtain an authorisation string`() {
        merchantService.register("Nike")
        val authorisationCode = merchantService.login("Nike")
        assertTrue(authorisationCode.matches(".+".toRegex()))
    }

    @Test
    fun `validate that a registered merchant can login multiple times`() {
        merchantService.register("Nike")
        val authorisationCode = merchantService.login("Nike")
        assertTrue(authorisationCode.matches(".+".toRegex()))
        val newAuthorisationCode = merchantService.login("Nike")
        assertNotEquals(authorisationCode, newAuthorisationCode)
    }

    @Test
    fun `attempt to login with non-registered merchant throws exception`() {
        val exception = assertThrows<MerchantNotFoundException> {
            merchantService.login("Nike")
        }
        assertEquals("Merchant is not registered: Nike", exception.message)
    }

    @Test
    fun `check merchant registration`() {
        assertFalse(merchantService.isRegistered("Nike"))
        merchantService.register("Nike")
        assertTrue(merchantService.isRegistered("Nike"))
    }

    @Test
    fun `validate authorisation code checking`() {
        // unregistered merchant
        val exceptionNotRegistered = assertThrows<MerchantNotFoundException> {
            merchantService.checkAuthorisationCode("Nike", "XXX")
        }
        assertEquals("Merchant is not registered: Nike", exceptionNotRegistered.message)
        // registered merchant
        merchantService.register("Nike")
        val exceptionNotLoggedIn = assertThrows<AuthorisationException> {
            merchantService.checkAuthorisationCode("Nike", "XXX")
        }
        assertEquals("Merchant not logged in: Nike", exceptionNotLoggedIn.message)
        // registered and logged in merchant with invalid code
        val authorisationCode = merchantService.login("Nike")
        val exceptionInvalidCode = assertThrows<AuthorisationException> {
            merchantService.checkAuthorisationCode("Nike", "XXX")
        }
        assertEquals("Invalid authorisation code for merchant: Nike", exceptionInvalidCode.message)
        // with valid code
        merchantService.checkAuthorisationCode("Nike", authorisationCode)
    }
}

private fun MerchantServiceInMemoryImpl.count(): Int {
    return this.merchantNameToCodeMap.size
}

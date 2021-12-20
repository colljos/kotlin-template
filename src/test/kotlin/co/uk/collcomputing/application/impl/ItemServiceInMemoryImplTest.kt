package co.uk.collcomputing.application.impl

import co.uk.collcomputing.application.ItemService
import co.uk.collcomputing.application.MerchantService
import co.uk.collcomputing.application.exceptions.AuthorisationException
import co.uk.collcomputing.application.exceptions.DuplicateItemException
import co.uk.collcomputing.application.exceptions.ItemNotFoundException
import co.uk.collcomputing.application.exceptions.MerchantNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ItemServiceInMemoryImplTest {

    private lateinit var itemService: ItemService
    private lateinit var merchantService: MerchantService

    @BeforeEach
    fun beforeEach() {
        merchantService = MerchantServiceInMemoryImpl()
        itemService = ItemServiceInMemoryImpl(merchantService)
    }

    // AC: The merchant must be able to create an item
    @Test
    fun `successfully create a new item for a registered merchant`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        val item = itemService.createOrUpdate("Nike", authorisation, 1, "Air Jordan", "Air Jordan Mid SE")
        assertEquals(1, item.code)
        assertEquals("Air Jordan", item.title)
        assertEquals("Air Jordan Mid SE", item.description)
    }

    // AC: In case the item already exists for the given code, update its title and description instead.
    @Test
    fun `successfully update an existing item for a registered merchant`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        val item = itemService.createOrUpdate("Nike", authorisation, 1, "Air Jordan", "Air Jordan Mid SE")
        assertEquals(1, item.code)
        assertEquals("Air Jordan", item.title)
        assertEquals("Air Jordan Mid SE", item.description)
        val updatedItem = itemService.createOrUpdate("Nike", authorisation, 1, "Air Jordan", "Air Jordan Low SE")
        assertEquals(item.copy(description = "Air Jordan Low SE"), updatedItem)
    }

    @Test
    fun `attempt to create a new item for an unregistered merchant throws an exception`() {
        val exception = assertThrows<MerchantNotFoundException> {
            itemService.createOrUpdate("Adidas", "XXX", 1, "XXX", "XXX")
        }
        assertEquals("Merchant is not registered: Adidas", exception.message)
    }

    @Test
    fun `attempt to create a new item for registered merchant not logged in throws an exception`() {
        merchantService.register("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.createOrUpdate("Nike", "XXX", 1, "Air Jordan", "Air Jordan Mid SE")
        }
        assertEquals("Merchant not logged in: Nike", exception.message)
    }

    @Test
    fun `attempt to create a new item without for logged in registered merchant with an invalid authorisation code throws an exception`() {
        merchantService.register("Nike")
        merchantService.login("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.createOrUpdate("Nike", "INVALID-CODE", 1, "Air Jordan", "Air Jordan Mid SE")
        }
        assertEquals("Invalid authorisation code for merchant: Nike", exception.message)
    }

    @Test
    fun `attempt to create a new item using an existing item code throws an exception`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        itemService.createOrUpdate("Nike", authorisation, 1, "Air Jordan", "Air Jordan Mid SE")
        val exception = assertThrows<DuplicateItemException> {
            merchantService.register("Adidas")
            val authorisation2 = merchantService.login("Adidas")
            itemService.createOrUpdate("Adidas", authorisation2, 1, "Forum", "Forum Mid Shoes")
        }
        assertEquals("Item code 1 already assigned to another merchant.", exception.message)
    }

    // The merchant must be able to list the items added already
    @Test
    fun `successfully list all items for a registered merchant`() {
        merchantService.register("Nike")
        val authorisationNike = merchantService.login("Nike")
        merchantService.register("Adidas")
        val authorisationAdidas = merchantService.login("Adidas")
        assertEquals(0, itemService.list("Nike", authorisationNike).size)
        assertEquals(0, itemService.list("Adidas", authorisationAdidas).size)
        itemService.createOrUpdate("Nike", authorisationNike, 1, "Air Jordan 1", "Air Jordan 1 Mid SE")
        itemService.createOrUpdate("Nike", authorisationNike, 2, "Air Jordan 2", "Air Jordan 2 Mid SE")
        itemService.createOrUpdate("Nike", authorisationNike, 3, "Air Jordan 3", "Air Jordan 3 Mid SE")
        itemService.createOrUpdate("Adidas", authorisationAdidas, 4, "Forum", "Forum Mid Shoes")
        assertEquals(3, itemService.list("Nike", authorisationNike).size)
        assertEquals(1, itemService.list("Adidas", authorisationAdidas).size)
    }

    @Test
    fun `attempt to list all items for an unregistered merchant throws an exception`() {
        val exception = assertThrows<MerchantNotFoundException> {
            itemService.list("Nike", "AUTH01")
        }
        assertEquals("Merchant is not registered: Nike", exception.message)
    }

    @Test
    fun `attempt to list all items for a merchant not logged in throws an exception`() {
        merchantService.register("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.list("Nike", "XXX")
        }
        assertEquals("Merchant not logged in: Nike", exception.message)
    }

    @Test
    fun `attempt to list all items for a merchant with an invalid authorisation code throws an exception`() {
        merchantService.register("Nike")
        merchantService.login("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.list("Nike", "XXX")
        }
        assertEquals("Invalid authorisation code for merchant: Nike", exception.message)
    }

    // AC: The merchant must be able to delete an item
    @Test
    fun `successfully remove an item for a registered merchant`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        val newItem = itemService.createOrUpdate("Nike", authorisation, 1, "Air Jordan 1", "Air Jordan 1 Mid SE")
        assertEquals(1, itemService.list("Nike", authorisation).size)
        val deletedItem = itemService.delete("Nike", authorisation, 1)
        assertEquals(0, itemService.list("Nike", authorisation).size)
        assertEquals(newItem, deletedItem)
    }

    @Test
    fun `attempt to remove a nonexistent item for a registered merchant`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        val exception = assertThrows<ItemNotFoundException> {
            itemService.delete("Nike", authorisation, 1)
        }
        assertEquals("Item not found with code: 1", exception.message)
    }

    @Test
    fun `attempt to delete an item for an unregistered merchant throws an exception`() {
        val exception = assertThrows<MerchantNotFoundException> {
            itemService.delete("Nike", "AUTH01", 1)
        }
        assertEquals("Merchant is not registered: Nike", exception.message)
    }

    @Test
    fun `attempt to delete an item for a registered merchant not logged in throws an exception`() {
        merchantService.register("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.delete("Nike", "XXX", 1)
        }
        assertEquals("Merchant not logged in: Nike", exception.message)
    }

    @Test
    fun `attempt to delete an item for a registered logged in merchant without an authorisation code throws an exception`() {
        merchantService.register("Nike")
        merchantService.login("Nike")
        val exception = assertThrows<AuthorisationException> {
            itemService.delete("Nike", "INVALID-CODE", 1)
        }
        assertEquals("Invalid authorisation code for merchant: Nike", exception.message)
    }
}
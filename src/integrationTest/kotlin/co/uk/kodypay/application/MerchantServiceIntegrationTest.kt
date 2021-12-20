package co.uk.kodypay.application

import co.uk.collcomputing.application.ItemService
import co.uk.collcomputing.application.MerchantService
import co.uk.collcomputing.application.exceptions.DuplicateMerchantException
import co.uk.collcomputing.application.exceptions.ItemNotFoundException
import co.uk.collcomputing.application.exceptions.MerchantNotFoundException
import co.uk.collcomputing.application.impl.ItemServiceInMemoryImpl
import co.uk.collcomputing.application.impl.MerchantServiceInMemoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class MerchantServiceIntegrationTest {

    private lateinit var itemService: ItemService
    private lateinit var merchantService: MerchantService

    companion object {
        const val ITEM_TITLE_AD = "Air Anthony Davis"
        const val ITEM_DESC_AD = "Air Anthony Davis Mid SE"
        const val ITEM_TITLE_JORDAN = "Air Jordan"
        const val ITEM_DESC_JORDAN = "Air Jordan Mid SE"
    }

    @BeforeEach
    fun beforeEach() {
        merchantService = MerchantServiceInMemoryImpl()
        itemService = ItemServiceInMemoryImpl(merchantService)
    }

    @Test
    fun `the merchant must be able to register with a given name`() {
        merchantService.register("Nike")
        merchantService.register("Adidas")
        merchantService.register("Converse")

        // The name must be guaranteed to be unique across all merchants.
        assertThrows<DuplicateMerchantException> {
            merchantService.register("Adidas")
        }
    }

    @Test
    fun `the merchant must be able to login with a given name`() {
        // The given name must have been registered prior.
        assertThrows<MerchantNotFoundException> {
            merchantService.login("Nike")
        }
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")

        // The authorisation gives access to the merchant to create, list, update and delete their items.
        itemService.createOrUpdate("Nike", authorisation, 1, ITEM_TITLE_JORDAN, ITEM_DESC_JORDAN)
        itemService.createOrUpdate("Nike", authorisation, 2, "Air Lebron", "Air Lebron Mid SE")
        itemService.createOrUpdate("Nike", authorisation, 3, "Air Davis", "Air Davis Mid SE")
        assertEquals(3, itemService.list("Nike", authorisation).size)
        val updatedItem = itemService.createOrUpdate("Nike", authorisation, 3, ITEM_TITLE_AD, ITEM_DESC_AD)
        assertEquals(ITEM_TITLE_AD, updatedItem.title)
        assertEquals(ITEM_DESC_AD, updatedItem.description)
        itemService.delete("Nike", authorisation, 2)
        assertEquals(2, itemService.list("Nike", authorisation).size)
    }

    @Test
    fun `the merchant must be able to create an item`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")

        // The Item created must have a UUID assigned upon successfully creating it.
        val newItem = itemService.createOrUpdate("Nike", authorisation, 1, ITEM_TITLE_JORDAN, ITEM_DESC_JORDAN)
        assertTrue(newItem.identifier.isUUID())

        // In case the item already exists for the given code, update its title and description instead.
        val updatedItem = itemService.createOrUpdate("Nike", authorisation, 1, ITEM_TITLE_AD, ITEM_DESC_AD)
        assertEquals(ITEM_TITLE_AD, updatedItem.title)
        assertEquals(ITEM_DESC_AD, updatedItem.description)
    }

    @Test
    fun `the merchant must be able to list the items added already`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")

        // In case of no items, return an empty array.
        assertTrue(itemService.list("Nike", authorisation).isEmpty())
        itemService.createOrUpdate("Nike", authorisation, 1, ITEM_TITLE_JORDAN, ITEM_DESC_JORDAN)
        itemService.createOrUpdate("Nike", authorisation, 2, "Air Lebron", "Air Lebron Mid SE")
        itemService.createOrUpdate("Nike", authorisation, 3, "Air Davis", "Air Davis Mid SE")
        assertEquals(3, itemService.list("Nike", authorisation).size)
    }

    @Test
    fun `the merchant must be able to delete an item`() {
        merchantService.register("Nike")
        val authorisation = merchantService.login("Nike")
        val newItem = itemService.createOrUpdate("Nike", authorisation, 1, ITEM_TITLE_JORDAN, ITEM_DESC_JORDAN)
        assertEquals(1, itemService.list("Nike", authorisation).size)

        // In case of a success, return the deleted Item.
        assertThrows<ItemNotFoundException> {
            itemService.delete("Nike", authorisation, 2)
        }
        val deletedItem = itemService.delete("Nike", authorisation, 1)
        assertEquals(newItem, deletedItem)
        assertTrue(itemService.list("Nike", authorisation).isEmpty())
    }
}

private fun UUID.isUUID(): Boolean =
    this.toString().matches("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})".toRegex())


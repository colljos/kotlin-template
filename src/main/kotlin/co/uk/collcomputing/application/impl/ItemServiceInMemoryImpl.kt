package co.uk.collcomputing.application.impl

import co.uk.collcomputing.application.ItemService
import co.uk.collcomputing.application.MerchantService
import co.uk.collcomputing.application.exceptions.DuplicateItemException
import co.uk.collcomputing.application.exceptions.ItemNotFoundException
import co.uk.collcomputing.application.exceptions.MerchantNotFoundException
import co.uk.collcomputing.domain.Item
import java.util.UUID

class ItemServiceInMemoryImpl(private val merchantService: MerchantService) : ItemService {

    private val items = mutableMapOf<Int, MerchantItem>()   // itemCode -> MerchantItem

    override fun createOrUpdate(
        merchantName: String,
        authorisation: String,
        itemCode: Int,
        itemTitle: String,
        itemDescription: String
    ): Item {
        validateMerchant(merchantName, authorisation)

        val existingItem = items[itemCode]
        if (existingItem == null) {
            // new Item
            items[itemCode] = MerchantItem(merchantName, Item(UUID.randomUUID(), itemCode, itemTitle, itemDescription))
        }
        else {
            // existing Item - check whether same merchant
            if (existingItem.merchantName != merchantName)
                throw DuplicateItemException(itemCode)
            items[itemCode] = MerchantItem(merchantName, existingItem.item.copy(title = itemTitle, description = itemDescription))
        }
        return items[itemCode]!!.item
    }

    override fun list(merchantName: String, authorisation: String): Array<Item> {
        validateMerchant(merchantName, authorisation)
        return items.filter { it.value.merchantName == merchantName }.map { it.value.item }.toTypedArray()
    }

    override fun delete(merchantName: String, authorisation: String, itemCode: Int): Item {
        validateMerchant(merchantName, authorisation)
        if (items[itemCode] == null)
            throw ItemNotFoundException(itemCode)
        val removedItem = items.remove(itemCode)
        return removedItem!!.item
    }

    private fun validateMerchant(merchantName: String, authorisation: String) {
        if (!merchantService.isRegistered(merchantName))
            throw MerchantNotFoundException(merchantName)
        merchantService.checkAuthorisationCode(merchantName, authorisation)    // throws AuthorisationException if invalid
    }
}

data class MerchantItem(val merchantName: String, val item: Item)

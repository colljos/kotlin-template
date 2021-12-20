package co.uk.collcomputing.application

import co.uk.collcomputing.domain.Item

interface ItemService
{
    /**
    Creates an Item for the given merchant.

    In case the item with the given code already exists, update both its title and description.

    @precondition the merchant must exist
    @precondition the itemCode must be unique across merchants

    @param merchantName: the name that the merchant registered with, using MerchantService#register
    @param authorisation: the authorisation as returned by MerchantService#login
    @param itemCode: the code of the item
    @param itemTitle: the title of the item
    @param itemDescription: the description of the item

    @return the item that was created or updated for the given merchant

    @throws DuplicateItemException in case the Item is already assigned to another merchant
    @throws MerchantNotFoundException in case the given merchant does not exist
    @throws AuthorisationException in case the given authorisation is invalid
     */
    fun createOrUpdate(merchantName: String, authorisation: String, itemCode: Int, itemTitle: String, itemDescription: String): Item

    /**
    Returns the list of Item for a given merchant.

    @precondition the given merchant must exist
    @precondition the authorisation is issued for the given merchant

    @param merchantName: the name that the merchant registered with, using MerchantService#register
    @param authorisation: the authorisation as returned by MerchantService#login

    @return a list of Items for the given merchant

    @throws MerchantNotFoundException in case the given merchant does not exist.
    @throws AuthorisationException in case the given authorisation is invalid.
     */
    fun list(merchantName: String, authorisation: String): Array<Item>


    /**
    Deletes the item with the given code for the given merchant.

    @precondition the given merchant must exist
    @precondition the authorisation is issued for the given merchant

    @param merchantName: the name that the merchant registered with, using MerchantService#register
    @param authorisation: the authorisation as returned by MerchantService#login
    @param itemCode: the code of the item

    @return the item that was deleted for the given merchant

    @throws ItemNotFoundException in case the Item is not found.
    @throws MerchantNotFoundException in case the given merchant does not exist.
    @throws AuthorisationException in case the given authorisation is invalid.
     */
    fun delete(merchantName: String, authorisation: String, itemCode: Int) : Item
}
package co.uk.collcomputing.application

interface MerchantService
{
    /**
    Registers a new merchant with the given merchant name.

    @param merchantName: the name the merchant wants to register with.
    @throws DuplicateMerchantException in case the given name is already registered with another merchant
     */
    fun register(merchantName: String)

    /**
    Logs in the merchant with the given name and returns an authorisation string.

    @precondition the given name must already be registered using #register
    @param merchantName: the name that the merchant is registered with
    @return authorisation string that can be used with the ItemService
    @throws MerchantNotFoundException in case the given merchant is not already registered
     */
    fun login(merchantName: String): String

    /**
    Checks whether a merchant is registered with the given merchant name.

    @param merchantName: the name of the merchant to check for registration.
    @return true if merchant is registered, otherwise false.
     */
    fun isRegistered(merchantName: String): Boolean

    /**
    Validate an authorisation code against that registered with a merchant.

    @precondition the given name must already be registered using #register
    @param merchantName: the name that the merchant is registered with
    @param merchantName: the authorisation code to validate against the merchant.
    @throws AuthorisationException in case the given merchant is not registered or not logged in.
     */
    fun checkAuthorisationCode(merchantName: String, authorisationCode: String)
}
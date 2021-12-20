package co.uk.collcomputing.application.impl

import co.uk.collcomputing.application.MerchantService
import co.uk.collcomputing.application.exceptions.AuthorisationException
import co.uk.collcomputing.application.exceptions.DuplicateMerchantException
import co.uk.collcomputing.application.exceptions.MerchantNotFoundException
import co.uk.collcomputing.application.utilities.SimpleAuthorisationCodeGenerator
import java.util.concurrent.ConcurrentHashMap

class MerchantServiceInMemoryImpl : MerchantService {

    internal val merchantNameToCodeMap: ConcurrentHashMap<String,String> = ConcurrentHashMap()

    companion object {
        const val NO_AUTH_CODE = "NO_AUTH_CODE"
    }

    override fun register(merchantName: String) {
        val entry = merchantNameToCodeMap.putIfAbsent(merchantName, NO_AUTH_CODE)
        if (entry != null)
            throw DuplicateMerchantException(merchantName)
    }

    override fun login(merchantName: String): String {
        if (!merchantNameToCodeMap.containsKey(merchantName))
            throw MerchantNotFoundException(merchantName)

        val authorisationCode = SimpleAuthorisationCodeGenerator.nextCode()
        merchantNameToCodeMap[merchantName] = authorisationCode

        return authorisationCode
    }

    override fun isRegistered(merchantName: String) = this.merchantNameToCodeMap.containsKey(merchantName)

    override fun checkAuthorisationCode(merchantName: String, authorisationCode: String) {
        if (!isRegistered(merchantName))
            throw MerchantNotFoundException(merchantName)
        val authorisation = merchantNameToCodeMap[merchantName]
        if (authorisation == NO_AUTH_CODE)
            throw AuthorisationException("Merchant not logged in: $merchantName")
        else if (authorisation != authorisationCode)
            throw AuthorisationException("Invalid authorisation code for merchant: $merchantName")
    }
}


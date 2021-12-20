package co.uk.collcomputing.application.exceptions

class MerchantNotFoundException(merchantName: String) : Exception("Merchant is not registered: $merchantName")
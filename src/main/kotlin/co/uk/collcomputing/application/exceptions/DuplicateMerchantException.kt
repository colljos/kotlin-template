package co.uk.collcomputing.application.exceptions

class DuplicateMerchantException(merchantName: String) : Exception("Merchant is already registered: $merchantName")
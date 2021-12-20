package co.uk.collcomputing.application.exceptions

class ItemNotFoundException(itemCode: Int) : Exception("Item not found with code: $itemCode")
package co.uk.collcomputing.application.exceptions

class DuplicateItemException(itemCode: Int) : Exception("Item code $itemCode already assigned to another merchant.")
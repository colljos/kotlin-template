package co.uk.collcomputing.domain

import java.util.UUID

data class Item (
    var identifier: UUID,
    var code: Int,
    var title: String,
    var description: String
)

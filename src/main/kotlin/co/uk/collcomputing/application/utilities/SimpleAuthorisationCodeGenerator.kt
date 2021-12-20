package co.uk.collcomputing.application.utilities

import java.util.concurrent.atomic.AtomicInteger

object SimpleAuthorisationCodeGenerator {
    private val codeCounter: AtomicInteger = AtomicInteger(0)

    fun nextCode(): String {
        return "AUTH${codeCounter.addAndGet(1)}"
    }
}
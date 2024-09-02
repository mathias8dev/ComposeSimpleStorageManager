package com.mathias8dev.composesimplestoragemanager.utils


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable


interface CoroutineScopeProvider : Closeable {
    val coroutineScope: CoroutineScope

    companion object {
        val Default = CoroutineScopeOwner()
    }
}

open class CoroutineScopeOwner : CoroutineScopeProvider {
    private var scope: CloseableCoroutineScope? =
        CloseableCoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val coroutineScope: CoroutineScope
        get() {
            val newScope = CloseableCoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
            if (scope == null) {
                scope = newScope
            }
            return newScope
        }

    override fun close() {
        scope?.cancel()
        scope = null
    }
}


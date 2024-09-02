package com.mathias8dev.composesimplestoragemanager.utils

import androidx.compose.runtime.RememberObserver
import kotlinx.coroutines.cancel


interface RememberCoroutineScopeProvider : CoroutineScopeProvider, RememberObserver {

    companion object {
        val Default = RememberCoroutineScopeOwner()
    }
}


class RememberCoroutineScopeOwner : CoroutineScopeOwner(), RememberCoroutineScopeProvider {
    override fun onAbandoned() {
        coroutineScope.cancel()
    }

    override fun onForgotten() {
        coroutineScope.cancel()
    }

    override fun onRemembered() {}
}
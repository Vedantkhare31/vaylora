package com.vaylora.app.core.result

sealed class VayloraResult<out T> {
    data class Success<T>(val data: T) : VayloraResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : VayloraResult<Nothing>()
    object Loading : VayloraResult<Nothing>()
}

inline fun <T> VayloraResult<T>.onSuccess(action: (T) -> Unit): VayloraResult<T> {
    if (this is VayloraResult.Success) action(data)
    return this
}

inline fun <T> VayloraResult<T>.onError(action: (String) -> Unit): VayloraResult<T> {
    if (this is VayloraResult.Error) action(message)
    return this
}

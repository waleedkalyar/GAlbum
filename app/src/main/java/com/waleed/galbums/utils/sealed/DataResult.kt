package com.waleed.galbums.utils.sealed

sealed class DataResult<T : Any> {
    class Success<T : Any>(val data: T) : DataResult<T>()
    class Loading<T : Any>(val title: String? = null) : DataResult<T>()
    class Error<T : Any>(val message: String? = null) : DataResult<T>()
}
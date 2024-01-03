package com.base.hilt.network

sealed class GraphQLResponseHandler<T>(
    val code: Int? = HttpErrorCode.NOT_DEFINED.code,
    val value: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : GraphQLResponseHandler<T>(value = data)
    class Error<T>(code: Int?, message: String?, data: T? = null) : GraphQLResponseHandler<T>(code, data, message)
    class Loading<T> : GraphQLResponseHandler<T>()
}
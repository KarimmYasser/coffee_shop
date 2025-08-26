package com.example.cofee_shop.core

sealed class ApiResult<out T> {
    data object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val exception: Throwable) : ApiResult<Nothing>()
}
package com.algokelvin.tiktokcoinmanager.core

sealed interface AppError {
    data object Connection : AppError
    data object Timeout : AppError
    data object Validation : AppError
    data object NotFound : AppError
    data object Server : AppError
    data class Unknown(val message: String) : AppError
}


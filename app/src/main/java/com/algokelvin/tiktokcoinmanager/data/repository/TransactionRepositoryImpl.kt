package com.algokelvin.tiktokcoinmanager.data.repository

import com.algokelvin.tiktokcoinmanager.core.AppError
import com.algokelvin.tiktokcoinmanager.core.Result
import com.algokelvin.tiktokcoinmanager.data.dto.ApiResponseDto
import com.algokelvin.tiktokcoinmanager.data.mapper.toDomain
import com.algokelvin.tiktokcoinmanager.data.mapper.toDto
import com.algokelvin.tiktokcoinmanager.data.remote.TransactionApiService
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class TransactionRepositoryImpl(
    private val apiService: TransactionApiService,
) : TransactionRepository {
    override fun getTransactions(): Flow<Result<List<Transaction>>> = flow {
        emit(safeApiCall { apiService.getTransactions().requireData().map { it.toDomain() } })
    }

    override fun getTransactionDetail(id: String): Flow<Result<Transaction>> = flow {
        emit(safeApiCall { apiService.getTransactionDetail(id).requireData().toDomain() })
    }

    override suspend fun createTransaction(input: TransactionInput): Result<Transaction> =
        safeApiCall { apiService.createTransaction(input.toDto()).requireData().toDomain() }

    override suspend fun updateTransaction(id: String, input: TransactionInput): Result<Transaction> =
        safeApiCall { apiService.updateTransaction(id, input.toDto()).requireData().toDomain() }

    override suspend fun deleteTransaction(id: String): Result<Unit> =
        safeApiCall {
            apiService.deleteTransaction(id)
            Unit
        }

    override fun getDashboardSummary(): Flow<Result<DashboardSummary>> = flow {
        emit(safeApiCall { apiService.getDashboardSummary().requireData().toDomain() })
    }
}

private fun <T> ApiResponseDto<T>.requireData(): T {
    if (!success || data == null) throw IllegalStateException(message)
    return data
}

private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (error: UnknownHostException) {
        Result.Failure(AppError.Connection)
    } catch (error: SocketTimeoutException) {
        Result.Failure(AppError.Timeout)
    } catch (error: HttpException) {
        Result.Failure(
            when (error.code()) {
                400, 422 -> AppError.Validation
                404 -> AppError.NotFound
                in 500..599 -> AppError.Server
                else -> AppError.Unknown(error.message())
            },
        )
    } catch (error: Throwable) {
        Result.Failure(AppError.Unknown(error.message.orEmpty()))
    }
}


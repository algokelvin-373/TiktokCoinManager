package com.algokelvin.tiktokcoinmanager.data.remote

import com.algokelvin.tiktokcoinmanager.data.dto.ApiResponseDto
import com.algokelvin.tiktokcoinmanager.data.dto.DashboardSummaryDto
import com.algokelvin.tiktokcoinmanager.data.dto.TransactionDto
import com.algokelvin.tiktokcoinmanager.data.dto.TransactionRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionApiService {
    @GET("api/v1/transactions")
    suspend fun getTransactions(): ApiResponseDto<List<TransactionDto>>

    @GET("api/v1/transactions/{id}")
    suspend fun getTransactionDetail(@Path("id") id: String): ApiResponseDto<TransactionDto>

    @POST("api/v1/transactions")
    suspend fun createTransaction(@Body body: TransactionRequestDto): ApiResponseDto<TransactionDto>

    @PUT("api/v1/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body body: TransactionRequestDto,
    ): ApiResponseDto<TransactionDto>

    @DELETE("api/v1/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): ApiResponseDto<Unit>

    @GET("api/v1/dashboard/summary")
    suspend fun getDashboardSummary(): ApiResponseDto<DashboardSummaryDto>
}


package com.floatpet.floatapp.ui.crypto

import retrofit2.http.GET
import retrofit2.http.Query

data class PriceResponse(
    val prices: Map<String, Double>
)

data class CryptoToken(
    val symbol: String,
    val name: String,
    val address: String,
    val price: Double? = null
)

interface JupiterApi {
    @GET("api/v1/prices?list_address=So11111111111111111111111111111111111111112")
    suspend fun getSolanaPrice(): PriceResponse
}

interface JupiterExtendedApi {
    @GET("api/v1/prices")
    suspend fun getPrices(@Query("list_address") addresses: String): PriceResponse
}
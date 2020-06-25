package au.cmcmarkets.ticker.data.api

import retrofit2.http.GET

interface BitcoinApi {

    @GET("ticker")
    suspend fun getPrice(): BitcoinPriceData

}

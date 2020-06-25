package au.cmcmarkets.ticker.data.api

import com.google.gson.annotations.SerializedName

data class BitcoinPriceData(
        @SerializedName("AUD")
        val bitcoinPrice: BitcoinPrice?
) {
        companion object {
                fun dummy() = BitcoinPriceData(BitcoinPrice.dummy())
        }
}

data class BitcoinPrice(
        @SerializedName("15m")
        val delayedValue: Float?,
        @SerializedName("last")
        val latestValue: Float?,
        @SerializedName("buy")
        val buyPrice: Float?,
        @SerializedName("sell")
        val sellPrice: Float?,
        @SerializedName("symbol")
        val symbol: String?
) {
        companion object {
                fun dummy() = BitcoinPrice(10.0f, 10.0f, 10.0f, 12.0f, "$")
        }
}
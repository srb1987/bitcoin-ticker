package au.cmcmarkets.ticker.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface BitcoinServer {
    fun pollBitcoinPrice(period: Long): Flow<ResultData<BitcoinPriceData>>
    fun stopPoll()
}

class BitcoinServerImpl @Inject constructor(private val bitcoinApi: BitcoinApi) : BitcoinServer {

    private val ioDispatcher = Dispatchers.IO
    private var shouldStopPolling = false

    @ExperimentalCoroutinesApi
    override fun pollBitcoinPrice(period: Long): Flow<ResultData<BitcoinPriceData>> {
        shouldStopPolling = false
        return channelFlow<ResultData<BitcoinPriceData>> {
            while (!shouldStopPolling) {
                val result = bitcoinApi.getPrice()
                send(ResultData.Success(result))
                delay(period)
            }
        }.flowOn(ioDispatcher)
    }

    override fun stopPoll() {
        shouldStopPolling = true
        ioDispatcher.cancel()
    }
}
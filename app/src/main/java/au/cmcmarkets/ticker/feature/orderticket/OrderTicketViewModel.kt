package au.cmcmarkets.ticker.feature.orderticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.cmcmarkets.ticker.data.api.BitcoinPrice
import au.cmcmarkets.ticker.data.api.BitcoinServer
import au.cmcmarkets.ticker.data.api.ResultData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

interface OrderTicketViewModelInterface {
    val bitcoinPrice: LiveData<BitcoinPrice>
    val errorMessage: LiveData<String>
    val showLoading: LiveData<Boolean>
    val totalValue: LiveData<String>
    val tradeActionEnabled: LiveData<Boolean>
    val tradeSelected: LiveData<TradeType>
    fun onResume()
    fun onPause()
    fun onTextChanged(units: String)
    fun onBuyPressed()
    fun onSellPressed()
}

enum class TradeType {
    Buy, Sell
}

fun ViewModel.getViewModelScope(coroutineScope: CoroutineScope?) = coroutineScope ?: this.viewModelScope

class OrderTicketViewModel @Inject constructor(private val providedCoroutineScope: CoroutineScope? = null, private val bitcoinServer: BitcoinServer) : ViewModel(), OrderTicketViewModelInterface {
    private var _bitcoinPrice = MutableLiveData<BitcoinPrice>()
    override val bitcoinPrice: LiveData<BitcoinPrice> = _bitcoinPrice

    private var _errorMessage = MutableLiveData<String>()
    override val errorMessage: LiveData<String> = _errorMessage

    private var _showLoading = MutableLiveData<Boolean>()
    override val showLoading: LiveData<Boolean> = _showLoading

    private var _totalValue = MutableLiveData<String>()
    override val totalValue: LiveData<String> = _totalValue

    private var _tradeActionEnabled = MutableLiveData<Boolean>()
    override val tradeActionEnabled: LiveData<Boolean> = _tradeActionEnabled

    private var _tradeSelected = MutableLiveData<TradeType>()
    override val tradeSelected: LiveData<TradeType> = _tradeSelected

    private val period : Long = 15000

    private var tradeType: TradeType = TradeType.Buy
    private var currentUnits: String = "0"

    private val coroutineScope = getViewModelScope(providedCoroutineScope)

    private var currentBitcoinPrice: BitcoinPrice? = null

    private fun updateTotalValue(units: String = currentUnits) {
        currentUnits = units
        val price = when (tradeType) {
            TradeType.Sell -> currentBitcoinPrice?.sellPrice
            TradeType.Buy -> currentBitcoinPrice?.buyPrice
        }
        price?.let {
            var totalValue = 0.0f
            try {
                totalValue = units.toFloat() * it
                _totalValue.postValue(totalValue.toString())
                _tradeActionEnabled.postValue(totalValue > 0)
            } catch (e : NumberFormatException) {
                _tradeActionEnabled.postValue(false)
            }
        }
    }

    override fun onResume() {
        _showLoading.postValue(true)
        _tradeSelected.postValue(tradeType)
        coroutineScope.launch {
            try {
                bitcoinServer.pollBitcoinPrice(period).collect { response ->
                    when (response) {
                        is ResultData.Success -> {
                            _showLoading.postValue(false)
                            currentBitcoinPrice = response.data.bitcoinPrice
                            _bitcoinPrice.postValue(currentBitcoinPrice)
                        }
                        is ResultData.Error -> {
                            _showLoading.postValue(false)
                            _errorMessage.postValue(response.exception.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                _showLoading.postValue(false)
                _errorMessage.postValue(e.message)
            }
        }
    }

    override fun onPause() {
        bitcoinServer.stopPoll()
    }

    override fun onTextChanged(units: String) {
        currentBitcoinPrice?.let {
            updateTotalValue(units)
        }
    }

    override fun onBuyPressed() {
        tradeType = TradeType.Buy
        _tradeSelected.postValue(tradeType)
        updateTotalValue()
    }

    override fun onSellPressed() {
        tradeType = TradeType.Sell
        _tradeSelected.postValue(tradeType)
        updateTotalValue()
    }

}
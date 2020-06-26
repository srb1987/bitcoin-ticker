package au.cmcmarkets.ticker.feature.orderticket

import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import au.cmcmarkets.ticker.R
import au.cmcmarkets.ticker.core.di.viewmodel.ViewModelFactory
import au.cmcmarkets.ticker.data.api.BitcoinPrice
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_order_ticket.*
import javax.inject.Inject

class OrderTicketFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OrderTicketViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order_ticket, container, false)

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        startObserving()
        addViewListeners()
        viewModel.onResume()
    }

    private fun addViewListeners() {
        units_input.doAfterTextChanged {
            it?.let {
                viewModel.onUnitsChanged(it.toString())
            }
        }
        total_value.doAfterTextChanged {
            it?.let {
                viewModel.onAmountChanged(it.toString())
            }
        }
        sell_price_button.setOnClickListener {
            viewModel.onSellPressed()
        }
        buy_price_button.setOnClickListener {
            viewModel.onBuyPressed()
        }
    }

    private fun startObserving() {
        viewModel.showLoading.observe(this, Observer {
            it?.let {
                toggleLoadingState(it)
            }
        })

        viewModel.bitcoinPrice.observe(this, Observer {
            it?.let {
                updateDetails(it)
            }
        })

        viewModel.errorMessage.observe(this, Observer {
            it?.let {
                showError(it)
            }
        })

        viewModel.units.observe(this, Observer {
            it?.let {
                units_input.setText(it)
            }
        })

        viewModel.totalValue.observe(this, Observer {
            it?.let {
                total_value.setText(it)
            }
        })

        viewModel.tradeActionEnabled.observe(this, Observer {
            it?.let {
                cancel_button.isEnabled = it
                confirm_button.isEnabled = it
            }
        })

        viewModel.tradeSelected.observe(this, Observer {
            when(it) {
                TradeType.Buy -> {
                    trade_type.text = getString(R.string.buy)
                    sell_price_button.isSelected = false
                    buy_price_button.isSelected = true
                }
                TradeType.Sell -> {
                    trade_type.text = getString(R.string.sell)
                    sell_price_button.isSelected = true
                    buy_price_button.isSelected = false
                }
                else -> {}
            }
        })
    }

    private fun toggleLoadingState(loading: Boolean) {
        if (loading) {
            buy_price.text = "--"
            sell_price.text = "--"
            units_input.setText("0")
            spread_text.text = "--"
            cancel_button.isEnabled = false
            confirm_button.isEnabled = false
            total_value_header.text = getString(R.string.amount)
            progress_bar.visibility = VISIBLE
        } else {
            progress_bar.visibility = GONE
        }
        units_input.isEnabled = !loading
    }

    private fun updateDetails(bitcoinPrice: BitcoinPrice) {
        buy_price.text = getFormattedString(String.format(getString(R.string.stock_price_text), bitcoinPrice.buyPrice))
        sell_price.text = getFormattedString(String.format(getString(R.string.stock_price_text), bitcoinPrice.sellPrice))
        total_value_header.text = String.format(getString(R.string.formatted_amount_header, bitcoinPrice.symbol))
        spread_text.text = (bitcoinPrice.buyPrice - bitcoinPrice.sellPrice).toString()
    }

    private fun getFormattedString(string: String) : SpannableString {
        val spannableString = SpannableString(string)
        spannableString.setSpan(RelativeSizeSpan(0.75f), string.length - 2, string.length, 0)
        return spannableString
    }

    private fun showError(errorMessage: String) {
        // TODO:
    }
}

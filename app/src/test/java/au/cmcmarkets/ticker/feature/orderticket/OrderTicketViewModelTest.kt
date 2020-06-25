package au.cmcmarkets.ticker.feature.orderticket

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import au.cmcmarkets.ticker.data.api.BitcoinPriceData
import au.cmcmarkets.ticker.data.api.BitcoinServer
import au.cmcmarkets.ticker.data.api.ResultData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OrderTicketViewModelTest {
    private lateinit var mockServer: BitcoinServer
    private lateinit var viewModel : OrderTicketViewModel
    private lateinit var testScope: CoroutineScope

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        testScope = TestCoroutineScope(TestCoroutineDispatcher())
        mockServer = mockk()
        every { mockServer.pollBitcoinPrice(any()) } returns flowOf(ResultData.Success(BitcoinPriceData.dummy()))
        every { mockServer.stopPoll() } returns Unit

        viewModel = OrderTicketViewModel(testScope, mockServer)
        viewModel.tradeSelected.observeForever {}
        viewModel.bitcoinPrice.observeForever {}
        viewModel.totalValue.observeForever {}
        viewModel.tradeActionEnabled.observeForever {}
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun onResume() {
        // given initial state

        //when
        viewModel.onResume()

        // then
        assertEquals(TradeType.Buy, viewModel.tradeSelected.value) // default
        assertEquals(10.0f, viewModel.bitcoinPrice.value?.buyPrice)
    }

    @Test
    fun onPause() {
        // given any state

        //when
        viewModel.onPause()

        // then
        verify { mockServer.stopPoll() }
    }

    @Test
    fun onTextChanged() {
        // given
        viewModel.onResume()

        // when
        viewModel.onTextChanged("10")

        // then
        assertEquals(100.0f.toString(), viewModel.totalValue.value)
        assertTrue(viewModel.tradeActionEnabled.value!!)
    }

    @Test
    fun onBuyPressed() {
        // given
        viewModel.onResume()

        // when
        viewModel.onBuyPressed()

        // then
        assertEquals(TradeType.Buy, viewModel.tradeSelected.value)
    }

    @Test
    fun onSellPressed() {
        // given
        viewModel.onResume()

        // when
        viewModel.onSellPressed()

        // then
        assertEquals(TradeType.Sell, viewModel.tradeSelected.value)
    }
}
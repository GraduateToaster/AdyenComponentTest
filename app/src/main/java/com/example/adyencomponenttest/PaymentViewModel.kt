package com.example.adyencomponenttest

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adyen.checkout.components.core.ActionComponentData
import com.adyen.checkout.components.core.Amount
import com.adyen.checkout.components.core.CheckoutConfiguration
import com.adyen.checkout.components.core.ComponentCallback
import com.adyen.checkout.components.core.ComponentError
import com.adyen.checkout.components.core.Configuration
import com.adyen.checkout.components.core.PaymentMethod
import com.adyen.checkout.core.Environment
import com.adyen.checkout.googlepay.GooglePayButtonStyling
import com.adyen.checkout.googlepay.GooglePayButtonTheme
import com.adyen.checkout.googlepay.GooglePayButtonType
import com.adyen.checkout.googlepay.GooglePayComponentState
import com.adyen.checkout.googlepay.googlePay
import com.google.android.gms.wallet.WalletConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import java.util.UUID

class PaymentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun getGoogleComponent() {
        val config = checkoutConfiguration()
        val callback = googleComponentCallBack()

        _uiState.update {
            it.copy(
                googleComponentData = GooglePayment(
                    checkoutConfig = config,
                    paymentMethod = getPaymentMethod(),
                    googleCallback = callback
                )
            )
        }
    }

    fun refreshComponentId() {
        _uiState.update {
            it.copy(
                googleComponentData = it.googleComponentData?.copy(
                    componentId = UUID.randomUUID().toString()
                )
            )
        }
    }

    private fun googleComponentCallBack(): ComponentCallback<GooglePayComponentState> {
        return object : ComponentCallback<GooglePayComponentState> {
            override fun onAdditionalDetails(actionComponentData: ActionComponentData) {
            }

            override fun onError(componentError: ComponentError) {
                Log.d("Payment", "${componentError.exception}")
                refreshComponentId()
            }

            override fun onSubmit(state: GooglePayComponentState) {
                Log.d("Payment", "Submit")
            }
        }
    }

    private fun checkoutConfiguration(): CheckoutConfiguration {
        val amount = Amount().also {
            it.currency = CURRENCY
            it.value = 1234L
        }

        return CheckoutConfiguration(
            shopperLocale = Locale.UK,
            environment = Environment.TEST,
            clientKey = CLIENT_KEY,
            amount = amount,
            isSubmitButtonVisible = true
        ) {
            googlePay {
                setShopperLocale(Locale.UK)

                googlePayEnvironment = WalletConstants.ENVIRONMENT_TEST
                googlePayButtonStyling =
                    GooglePayButtonStyling(
                        GooglePayButtonTheme.DARK,
                        GooglePayButtonType.PAY
                    )
            }
        }
    }

    private fun getPaymentMethod(): PaymentMethod {
        return PaymentMethod(
            type = "googlepay",
            name = "Google Pay",
            configuration = Configuration(
                merchantId = MERCHANT_ID,
                gatewayMerchantId = GATEWAY_MERCHANT_ID,
            ),
        )
    }

    data class UiState(
        val googleComponentData: GooglePayment? = null
    )

    data class GooglePayment(
        val checkoutConfig: CheckoutConfiguration,
        val paymentMethod: PaymentMethod,
        val googleCallback: ComponentCallback<GooglePayComponentState>,
        val componentId: String = UUID.randomUUID().toString()
    )

    companion object {
        const val CLIENT_KEY = ""
        const val CURRENCY = "GBP"

        const val GATEWAY_MERCHANT_ID = ""
        const val MERCHANT_ID = ""
    }
}

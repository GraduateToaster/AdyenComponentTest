package com.example.adyencomponenttest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adyen.checkout.components.compose.AdyenComponent
import com.adyen.checkout.components.compose.get
import com.adyen.checkout.components.core.internal.Component
import com.adyen.checkout.googlepay.GooglePayComponent
import com.adyen.checkout.ui.core.AdyenComponentView
import com.adyen.checkout.ui.core.internal.ui.ViewableComponent

@Composable
fun PaymentPage() {

    val viewModel: PaymentViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.getGoogleComponent() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Get Google Component")
            }

            state.googleComponentData?.let { method ->
                AdyenComponent(
                    component = GooglePayComponent.PROVIDER.get(
                        paymentMethod = method.paymentMethod,
                        checkoutConfiguration = method.checkoutConfig,
                        componentCallback = method.googleCallback,
                        key = method.componentId
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun <T> AdyenComponentFix(
    component: T,
    modifier: Modifier = Modifier,
) where T : ViewableComponent, T : Component {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            AdyenComponentView(it)
        },
        update = {
            it.attach(component, lifecycleOwner)
        },
        modifier = modifier,
    )
}

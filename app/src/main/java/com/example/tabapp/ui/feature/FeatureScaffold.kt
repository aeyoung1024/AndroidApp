package com.example.tabapp.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tabapp.data.Feature
import com.example.tabapp.ui.components.SaitTopBar
import com.example.tabapp.ui.inbound.InboundOrderListScreen

/** 모든 기능 화면이 공통으로 사용하는 틀 (상단바: 뒤로가기 + 제목 + SAIT 로고) */
@Composable
fun FeatureScaffold(
    feature: Feature,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SaitTopBar(title = feature.title, subtitle = feature.description, onBack = onBack)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            content()
        }
    }
}

/** 각 기능 화면을 route 에 맞게 연결 */
@Composable
fun FeatureScreen(
    feature: Feature,
    onBack: () -> Unit,
    onInboundOrderSelected: (String) -> Unit,
) {
    FeatureScaffold(feature = feature, onBack = onBack) {
        when (feature) {
            Feature.INBOUND -> InboundOrderListScreen(onOrderSelected = onInboundOrderSelected)
            Feature.ISSUE -> IssueScreen()
            Feature.TRANSFER -> TransferScreen()
            Feature.EXTERNAL_OUT -> ExternalOutScreen()
            Feature.INVENTORY -> InventoryScreen()
            Feature.SYNC -> SyncScreen()
        }
    }
}

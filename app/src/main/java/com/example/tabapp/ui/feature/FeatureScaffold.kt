package com.example.tabapp.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.tabapp.data.Feature

/** 모든 기능 화면이 공통으로 사용하는 상단바(뒤로가기 + 제목) 틀 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScaffold(
    feature: Feature,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(feature.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = feature.color,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
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
fun FeatureScreen(feature: Feature, onBack: () -> Unit) {
    FeatureScaffold(feature = feature, onBack = onBack) {
        when (feature) {
            Feature.INBOUND -> InboundScreen()
            Feature.ISSUE -> IssueScreen()
            Feature.TRANSFER -> TransferScreen()
            Feature.EXTERNAL_OUT -> ExternalOutScreen()
            Feature.INVENTORY -> InventoryScreen()
            Feature.SYNC -> SyncScreen()
        }
    }
}

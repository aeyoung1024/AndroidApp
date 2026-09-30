package com.example.tabapp.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tabapp.data.Feature
import com.example.tabapp.ui.theme.TabAppTheme
import com.example.tabapp.ui.theme.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userName: String,
    onFeatureClick: (Feature) -> Unit,
    onLogout: () -> Unit,
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("메인 메뉴", fontWeight = FontWeight.Bold) },
                actions = {
                    Text(
                        text = "$userName 님",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "로그아웃")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        // 가로: 3열 x 2행, 세로: 2열 x 3행
        val columns = if (isLandscape()) 3 else 2
        FeatureGrid(
            features = Feature.entries,
            columns = columns,
            onFeatureClick = onFeatureClick,
            contentPadding = innerPadding,
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text("로그아웃") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("취소") }
            },
        )
    }
}

/**
 * 버튼들이 화면을 가득 채우도록 행/열에 weight 를 나눠 배치합니다.
 * 스크롤 없이 6개 버튼이 한 화면에 모두 보입니다.
 */
@Composable
private fun FeatureGrid(
    features: List<Feature>,
    columns: Int,
    onFeatureClick: (Feature) -> Unit,
    contentPadding: PaddingValues,
) {
    val spacing = 20.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        features.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                rowItems.forEach { feature ->
                    FeatureButton(
                        feature = feature,
                        onClick = { onFeatureClick(feature) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                // 마지막 줄이 비는 경우 자리 맞춤
                repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun FeatureButton(
    feature: Feature,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = feature.color),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = feature.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = feature.description,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "가로", widthDp = 1280, heightDp = 800)
@Composable
private fun MainLandscapePreview() {
    TabAppTheme { MainScreen(userName = "admin", onFeatureClick = {}, onLogout = {}) }
}

@Preview(name = "세로", widthDp = 800, heightDp = 1280)
@Composable
private fun MainPortraitPreview() {
    TabAppTheme { MainScreen(userName = "admin", onFeatureClick = {}, onLogout = {}) }
}

package com.example.tabapp.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tabapp.data.Feature
import com.example.tabapp.ui.components.SaitCard
import com.example.tabapp.ui.components.SaitLogo
import com.example.tabapp.ui.theme.TabAppTheme
import com.example.tabapp.ui.theme.isLandscape
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreen(
    userName: String,
    onFeatureClick: (Feature) -> Unit,
    onLogout: () -> Unit,
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    val landscape = isLandscape()
    // 화면 높이가 낮으면(작은 태블릿 가로 등) 머리글·여백을 줄여 버튼 공간 확보
    val compact = LocalConfiguration.current.screenHeightDp < 700
    val horizontalPadding = if (landscape) 40.dp else 32.dp

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = horizontalPadding),
        ) {
            Header(userName = userName, compact = compact, onLogoutClick = { showLogoutDialog = true })
            Greeting(userName = userName, compact = compact)

            // 가로: 3열 x 2행, 세로: 2열 x 3행 (스크롤 없이 한 화면)
            FeatureGrid(
                features = Feature.entries,
                columns = if (landscape) 3 else 2,
                spacing = if (compact) 16.dp else 20.dp,
                onFeatureClick = onFeatureClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = if (compact) 20.dp else 32.dp),
            )
        }
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

@Composable
private fun Header(userName: String, compact: Boolean, onLogoutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (compact) 12.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SaitLogo(height = 22.dp)
        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(20.dp),
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = "자재관리 시스템",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        // 사용자 정보 + 로그아웃
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = userName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "$userName 님",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.width(20.dp))
        OutlinedButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("로그아웃")
        }
    }
}

@Composable
private fun Greeting(userName: String, compact: Boolean) {
    val today = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN))
    }
    Column(
        modifier = Modifier.padding(
            top = if (compact) 4.dp else 12.dp,
            bottom = if (compact) 16.dp else 28.dp,
        ),
    ) {
        Text(
            text = "안녕하세요, ${userName}님",
            style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(if (compact) 2.dp else 6.dp))
        Text(
            text = "$today  ·  작업할 메뉴를 선택하세요.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** 버튼들이 남은 공간을 가득 채우도록 행/열에 weight 를 나눠 배치 */
@Composable
private fun FeatureGrid(
    features: List<Feature>,
    columns: Int,
    spacing: Dp,
    onFeatureClick: (Feature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
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
                    FeatureTile(
                        feature = feature,
                        onClick = { onFeatureClick(feature) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/**
 * 기능 타일. 타일 높이에 따라 배치를 자동으로 바꿉니다.
 *  - 높이가 충분하면: 위 아이콘 / 아래 제목·설명 (세로형)
 *  - 높이가 낮으면: 왼쪽 아이콘 / 오른쪽 제목·설명 (가로형) → 글자가 잘리지 않음
 */
@Composable
private fun FeatureTile(
    feature: Feature,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaitCard(onClick = onClick, modifier = modifier, cornerRadius = 24.dp) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxHeight >= 190.dp) {
                TallTileContent(feature)
            } else {
                CompactTileContent(feature, iconSize = if (maxHeight >= 96.dp) 52.dp else 40.dp)
            }
        }
    }
}

@Composable
private fun TallTileContent(feature: Feature) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        FeatureIcon(feature, size = 60.dp)
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.Bottom) {
            FeatureTexts(
                feature = feature,
                titleStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            ArrowBadge()
        }
    }
}

@Composable
private fun CompactTileContent(feature: Feature, iconSize: Dp) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeatureIcon(feature, size = iconSize)
        Spacer(Modifier.width(16.dp))
        FeatureTexts(
            feature = feature,
            titleStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        ArrowBadge()
    }
}

/** 연한 색 배경의 라운드 사각형 아이콘 */
@Composable
private fun FeatureIcon(feature: Feature, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(feature.color.copy(alpha = 0.12f), RoundedCornerShape(size * 0.3f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = feature.icon,
            contentDescription = null,
            tint = feature.color,
            modifier = Modifier.size(size * 0.53f),
        )
    }
}

@Composable
private fun FeatureTexts(feature: Feature, titleStyle: TextStyle, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = feature.title,
            style = titleStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = feature.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ArrowBadge() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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

@Preview(name = "작은 태블릿 가로", widthDp = 960, heightDp = 600)
@Composable
private fun MainSmallLandscapePreview() {
    TabAppTheme { MainScreen(userName = "admin", onFeatureClick = {}, onLogout = {}) }
}

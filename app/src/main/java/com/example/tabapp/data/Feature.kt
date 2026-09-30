package com.example.tabapp.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 메인 화면의 6개 기능 정의.
 * 버튼 이름/아이콘/색상은 여기서만 바꾸면 메인 화면과 상세 화면에 모두 반영됩니다.
 */
enum class Feature(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
) {
    INBOUND("inbound", "입고", "자재 입고 등록", Icons.Filled.AddCircle, Color(0xFF1E88E5)),
    ISSUE("issue", "불출", "자재 불출 처리", Icons.Filled.ShoppingCart, Color(0xFFFB8C00)),
    TRANSFER("transfer", "이동", "창고·위치 간 자재 이동", Icons.AutoMirrored.Filled.ArrowForward, Color(0xFF43A047)),
    EXTERNAL_OUT("external_out", "사외반출", "회사 외부로 자재 반출", Icons.AutoMirrored.Filled.Send, Color(0xFFE53935)),
    INVENTORY("inventory", "재고현황", "현재 재고 조회", Icons.AutoMirrored.Filled.List, Color(0xFF00897B)),
    SYNC("sync", "동기화", "서버와 데이터 동기화", Icons.Filled.Refresh, Color(0xFF546E7A)),
}

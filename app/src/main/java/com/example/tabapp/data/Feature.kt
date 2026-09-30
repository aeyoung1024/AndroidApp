package com.example.tabapp.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
    FEATURE_1("feature1", "기능 1", "첫 번째 기능 설명", Icons.AutoMirrored.Filled.List, Color(0xFF1E88E5)),
    FEATURE_2("feature2", "기능 2", "두 번째 기능 설명", Icons.Filled.Search, Color(0xFF43A047)),
    FEATURE_3("feature3", "기능 3", "세 번째 기능 설명", Icons.Filled.DateRange, Color(0xFFFB8C00)),
    FEATURE_4("feature4", "기능 4", "네 번째 기능 설명", Icons.Filled.Person, Color(0xFF8E24AA)),
    FEATURE_5("feature5", "기능 5", "다섯 번째 기능 설명", Icons.Filled.Info, Color(0xFF00897B)),
    FEATURE_6("feature6", "기능 6", "여섯 번째 기능 설명", Icons.Filled.Settings, Color(0xFF546E7A)),
}

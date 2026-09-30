package com.example.tabapp.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tabapp.data.Feature

/** 아직 구현되지 않은 기능 화면에 표시하는 임시 내용 */
@Composable
fun PlaceholderContent(feature: Feature) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = feature.icon,
            contentDescription = null,
            tint = feature.color,
            modifier = Modifier.size(96.dp),
        )
        Text(
            text = "${feature.title} 화면",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = feature.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

package com.example.tabapp.ui.inbound

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tabapp.data.inbound.OrderStatus
import com.example.tabapp.ui.theme.SuccessGreen
import com.example.tabapp.ui.theme.WarningOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DisplayDateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

/** 화면 표시용 날짜 형식 (예: 2026.09.30) */
fun LocalDate.display(): String = format(DisplayDateFormat)

@Composable
fun InfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val fg = when (status) {
        OrderStatus.PENDING -> WarningOrange
        OrderStatus.COMPLETED -> SuccessGreen
    }
    val bg = fg.copy(alpha = 0.12f)
    Surface(shape = RoundedCornerShape(50), color = bg) {
        Text(
            text = status.label,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

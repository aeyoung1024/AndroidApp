package com.example.tabapp.ui.inbound

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tabapp.data.inbound.InboundRepository
import com.example.tabapp.data.inbound.OrderStatus
import com.example.tabapp.data.inbound.PurchaseOrder
import com.example.tabapp.ui.components.SaitCard
import java.time.LocalDate

/** 발주 리스트 조회 기간 (발주일 기준) */
enum class OrderPeriod(val label: String, val months: Long?) {
    ONE_MONTH("1개월", 1),
    THREE_MONTHS("3개월", 3),
    SIX_MONTHS("6개월", 6),
    OVER_ONE_YEAR("1년 이상", null), // 기간 제한 없이 1년 이상 지난 발주까지 모두 조회

    ;

    /** 조회 시작일. null 이면 제한 없음 */
    fun startDate(today: LocalDate): LocalDate? = months?.let { today.minusMonths(it) }
}

enum class OrderFilter(val label: String, val status: OrderStatus?) {
    PENDING("미입고", OrderStatus.PENDING),
    COMPLETED("입고완료", OrderStatus.COMPLETED),
    ALL("전체", null),
}

/** 입고 첫 화면: 발주 리스트. 항목을 누르면 입고 등록 화면으로 이동 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundOrderListScreen(onOrderSelected: (String) -> Unit) {
    val orders by InboundRepository.orders.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(OrderFilter.PENDING) }
    var period by rememberSaveable { mutableStateOf(OrderPeriod.ONE_MONTH) }

    val today = remember { LocalDate.now() }
    val startDate = period.startDate(today)

    // 조회 기간 내 발주 (최신 발주일 순)
    val periodOrders = remember(orders, startDate) {
        orders
            .filter { startDate == null || !it.orderDate.isBefore(startDate) }
            .sortedByDescending { it.orderDate }
    }
    val filtered = remember(periodOrders, query, filter) {
        val q = query.trim()
        periodOrders
            .filter { filter.status == null || it.status == filter.status }
            .filter { o ->
                q.isEmpty() || listOf(o.orderNo, o.itemCode, o.itemName, o.supplier)
                    .any { it.contains(q, ignoreCase = true) }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        // 상단: 조회 기간 선택
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "조회 기간",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.widthIn(min = 360.dp)) {
                OrderPeriod.entries.forEachIndexed { index, p ->
                    SegmentedButton(
                        selected = period == p,
                        onClick = { period = p },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = OrderPeriod.entries.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            activeBorderColor = MaterialTheme.colorScheme.primary,
                            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        ),
                    ) {
                        Text(p.label)
                    }
                }
            }
            Text(
                text = if (startDate == null) "전체 기간" else "$startDate ~ $today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("발주번호, 품목, 거래처 검색") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "검색어 지우기")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.weight(1f),
            )
            OrderFilter.entries.forEach { f ->
                val count = periodOrders.count { f.status == null || it.status == f.status }
                FilterChip(
                    selected = filter == f,
                    onClick = { filter = f },
                    label = { Text("${f.label} $count") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "조회 기간 내 표시할 발주가 없습니다.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            // 가로(약 1280dp)에서는 3열, 세로(약 800dp)에서는 2열로 자동 배치
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 340.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(filtered, key = { it.orderNo }) { order ->
                    OrderCard(order = order, onClick = { onOrderSelected(order.orderNo) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: PurchaseOrder, onClick: () -> Unit) {
    SaitCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = order.orderNo,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(order.status)
            }
            Text(
                text = order.itemName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${order.itemCode} · ${order.supplier}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Row {
                InfoItem("발주량", "${order.quantity} 개", Modifier.weight(1f))
                InfoItem("발주일", order.orderDate.toString(), Modifier.weight(1f))
                InfoItem("납기일", order.dueDate.toString(), Modifier.weight(1f))
            }
        }
    }
}

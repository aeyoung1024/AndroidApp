package com.example.tabapp.ui.inbound

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tabapp.data.inbound.InboundRepository
import com.example.tabapp.data.inbound.OrderStatus
import com.example.tabapp.data.inbound.PurchaseOrder
import com.example.tabapp.ui.components.SaitCard
import com.example.tabapp.ui.theme.TabAppTheme
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

/** 이 너비 이상이면 표(테이블) 형태, 미만이면 2줄 목록 형태 */
private val WideLayoutMinWidth = 760.dp
private val ToolbarHeight = 44.dp

/** 입고 첫 화면: 발주 리스트. 항목을 누르면 입고 등록 화면으로 이동 */
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
    val counts = remember(periodOrders) {
        OrderFilter.entries.associateWith { f -> periodOrders.count { f.status == null || it.status == f.status } }
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
    val rangeText = if (startDate == null) "전체 기간" else "${startDate.display()} ~ ${today.display()}"

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    ) {
        val wide = maxWidth >= WideLayoutMinWidth

        Column(modifier = Modifier.fillMaxSize()) {
            // ── 조회 조건: 기간 + 검색 ──
            if (wide) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PeriodSelector(period = period, onSelect = { period = it })
                    Spacer(Modifier.width(16.dp))
                    RangeText(rangeText)
                    Spacer(Modifier.weight(1f))
                    SearchField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.width(320.dp),
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PeriodSelector(period = period, onSelect = { period = it })
                    Spacer(Modifier.width(16.dp))
                    RangeText(rangeText)
                }
                Spacer(Modifier.height(12.dp))
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── 발주 목록 ──
            SaitCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusTabs(selected = filter, counts = counts, onSelect = { filter = it })
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "총 ${filtered.size}건",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (filtered.isEmpty()) {
                    EmptyState(modifier = Modifier.fillMaxSize())
                } else {
                    if (wide) {
                        TableHeader()
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered, key = { it.orderNo }) { order ->
                            if (wide) {
                                WideOrderRow(order = order, onClick = { onOrderSelected(order.orderNo) })
                            } else {
                                CompactOrderRow(order = order, onClick = { onOrderSelected(order.orderNo) })
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ───────────────────────── 조회 조건 ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(period: OrderPeriod, onSelect: (OrderPeriod) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.height(ToolbarHeight)) {
        OrderPeriod.entries.forEachIndexed { index, p ->
            SegmentedButton(
                selected = period == p,
                onClick = { onSelect(p) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = OrderPeriod.entries.size),
                icon = {}, // 선택 체크 아이콘 없이 깔끔하게
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Text(p.label, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun RangeText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
    )
}

/** 기간 버튼과 높이를 맞춘 둥근 검색창 */
@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .height(ToolbarHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = "발주번호, 품목, 거래처 검색",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onValueChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "검색어 지우기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/** 미입고 / 입고완료 / 전체 탭 (건수 포함) */
@Composable
private fun StatusTabs(
    selected: OrderFilter,
    counts: Map<OrderFilter, Int>,
    onSelect: (OrderFilter) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        OrderFilter.entries.forEach { f ->
            val isSelected = f == selected
            val contentColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .clickable { onSelect(f) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(f.label, style = MaterialTheme.typography.titleSmall, color = contentColor)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${counts[f] ?: 0}",
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                )
            }
        }
    }
}

// ───────────────────────── 목록 ─────────────────────────

// 표 열 너비 비율 (헤더와 행이 같은 값을 사용해 세로 정렬을 맞춤)
private const val W_ITEM = 2.6f
private const val W_SUPPLIER = 1.5f
private const val W_QTY = 0.9f
private const val W_DATE = 1.2f
private const val W_STATUS = 1.1f
private val ChevronWidth = 24.dp

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderCell("품목 / 발주번호", W_ITEM)
        HeaderCell("거래처", W_SUPPLIER)
        HeaderCell("발주량", W_QTY, TextAlign.End)
        HeaderCell("발주일", W_DATE, TextAlign.Center)
        HeaderCell("납기일", W_DATE, TextAlign.Center)
        HeaderCell("상태", W_STATUS, TextAlign.Center)
        Spacer(Modifier.width(ChevronWidth))
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float, align: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
        modifier = Modifier.weight(weight),
    )
}

@Composable
private fun WideOrderRow(order: PurchaseOrder, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(W_ITEM)) {
            Text(
                text = order.itemName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${order.orderNo}  ·  ${order.itemCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BodyCell(order.supplier, W_SUPPLIER)
        Text(
            text = "${order.quantity}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(W_QTY),
        )
        BodyCell(order.orderDate.display(), W_DATE, TextAlign.Center)
        BodyCell(order.dueDate.display(), W_DATE, TextAlign.Center)
        Box(modifier = Modifier.weight(W_STATUS), contentAlignment = Alignment.Center) {
            StatusBadge(order.status)
        }
        Chevron()
    }
}

@Composable
private fun RowScope.BodyCell(text: String, weight: Float, align: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = align,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(weight),
    )
}

/** 좁은 화면(세로 등)용 2줄 목록 행 */
@Composable
private fun CompactOrderRow(order: PurchaseOrder, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.itemName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${order.orderNo}  ·  ${order.supplier}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "발주 ${order.orderDate.display()}  ·  납기 ${order.dueDate.display()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${order.quantity}개",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(6.dp))
            StatusBadge(order.status)
        }
        Chevron()
    }
}

@Composable
private fun Chevron() {
    Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(start = 4.dp)
            .size(ChevronWidth - 4.dp),
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text("조회된 발주가 없습니다.", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "조회 기간이나 검색어를 변경해 보세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "가로", widthDp = 1280, heightDp = 720, showBackground = true, backgroundColor = 0xFFF4F5F7)
@Composable
private fun InboundListLandscapePreview() {
    TabAppTheme { InboundOrderListScreen(onOrderSelected = {}) }
}

@Preview(name = "세로(작은 태블릿)", widthDp = 600, heightDp = 900, showBackground = true, backgroundColor = 0xFFF4F5F7)
@Composable
private fun InboundListPortraitPreview() {
    TabAppTheme { InboundOrderListScreen(onOrderSelected = {}) }
}

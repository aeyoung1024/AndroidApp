package com.example.tabapp.ui.inbound

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabapp.data.inbound.InboundRecord
import com.example.tabapp.data.inbound.PurchaseOrder
import com.example.tabapp.data.inbound.ScanField
import com.example.tabapp.ui.components.SaitCard
import com.example.tabapp.ui.components.SaitTopBar
import com.example.tabapp.ui.theme.SuccessGreen
import com.example.tabapp.ui.theme.isLandscape

/** 입고 등록 화면: 발주량만큼 생성된 레코드에 입고 위치 / 실린더 번호를 스캔 입력 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundRegisterScreen(
    onBack: () -> Unit,
    viewModel: InboundRegisterViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }

    val requestBack: () -> Unit = {
        if (state.isDirty) showExitDialog = true else onBack()
    }
    // 입력 중에 하드웨어 뒤로가기를 누르면 확인
    BackHandler(enabled = state.isDirty) { showExitDialog = true }

    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message.text)
        viewModel.consumeMessage(message.id)
    }
    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }
    LaunchedEffect(state.activeCell) {
        if (state.activeCell == null) focusManager.clearFocus()
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SaitTopBar(
                title = if (state.readOnly) "입고 내역" else "입고 등록",
                subtitle = state.order?.let { "${it.orderNo} · ${it.supplier}" },
                onBack = requestBack,
            )
        },
        bottomBar = {
            if (!state.readOnly && state.order != null) {
                SaveBar(
                    completed = state.completedCount,
                    total = state.records.size,
                    duplicateCount = state.duplicateCylinders.size,
                    canSave = state.canSave,
                    isSaving = state.isSaving,
                    onSave = { showSaveDialog = true },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val order = state.order
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text("발주 정보를 찾을 수 없습니다.", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (isLandscape()) {
            // 가로: 왼쪽 발주 정보 / 오른쪽 레코드 목록
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OrderInfoPanel(
                    order = order,
                    readOnly = state.readOnly,
                    sameLocation = state.sameLocation,
                    onSameLocationChange = viewModel::setSameLocation,
                    landscape = true,
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                )
                RecordTable(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        } else {
            // 세로: 위쪽 발주 정보 / 아래 레코드 목록
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OrderInfoPanel(
                    order = order,
                    readOnly = state.readOnly,
                    sameLocation = state.sameLocation,
                    onSameLocationChange = viewModel::setSameLocation,
                    landscape = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                RecordTable(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("입고 등록 취소") },
            text = { Text("입력한 내용이 저장되지 않습니다. 나가시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onBack()
                }) { Text("나가기") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("계속 입력") }
            },
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("입고 저장") },
            text = { Text("${state.order?.itemName ?: ""} ${state.records.size}건을 입고 처리하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    viewModel.save()
                }) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("취소") }
            },
        )
    }
}

@Composable
private fun OrderInfoPanel(
    order: PurchaseOrder,
    readOnly: Boolean,
    sameLocation: Boolean,
    onSameLocationChange: (Boolean) -> Unit,
    landscape: Boolean,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        "품목코드" to order.itemCode,
        "거래처" to order.supplier,
        "발주량" to "${order.quantity} 개",
        "발주일" to order.orderDate.toString(),
        "납기일" to order.dueDate.toString(),
        "상태" to order.status.label,
    )

    SaitCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .then(if (landscape) Modifier.verticalScroll(rememberScrollState()) else Modifier),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(order.itemName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = order.orderNo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (landscape) {
                items.forEach { (label, value) -> InfoItem(label, value) }
            } else {
                items.chunked(3).forEach { rowItems ->
                    Row {
                        rowItems.forEach { (label, value) -> InfoItem(label, value, Modifier.weight(1f)) }
                    }
                }
            }

            if (!readOnly) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("입고 위치 동일 적용", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "처음 스캔한 위치를 나머지 레코드에 자동 입력",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = sameLocation, onCheckedChange = onSameLocationChange)
                }
            }
        }
    }
}

private val SEQ_WIDTH = 44.dp
private val STATUS_WIDTH = 40.dp
private val CLEAR_WIDTH = 48.dp

@Composable
private fun RecordTable(
    state: InboundRegisterUiState,
    viewModel: InboundRegisterViewModel,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val duplicates = remember(state.records) { state.duplicateCylinders }
    val activeRow = state.activeCell?.row

    // 입력 대상 레코드가 화면 밖에 있으면 스크롤해서 보여줌
    LaunchedEffect(activeRow) {
        if (activeRow == null) return@LaunchedEffect
        val layout = listState.layoutInfo
        val fullyVisible = layout.visibleItemsInfo.any {
            it.index == activeRow && it.offset >= 0 && it.offset + it.size <= layout.viewportEndOffset
        }
        if (!fullyVisible) listState.animateScrollToItem((activeRow - 2).coerceAtLeast(0))
    }

    SaitCard(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HeaderText("No.", Modifier.width(SEQ_WIDTH), TextAlign.Center)
                HeaderText(ScanField.LOCATION.label, Modifier.weight(1f))
                HeaderText(ScanField.CYLINDER.label, Modifier.weight(1f))
                Spacer(Modifier.width(STATUS_WIDTH))
                if (!state.readOnly) Spacer(Modifier.width(CLEAR_WIDTH))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.records, key = { _, record -> record.seq }) { index, record ->
                    RecordRow(
                        record = record,
                        activeField = state.activeCell?.takeIf { it.row == index }?.field,
                        isDuplicate = record.cylinderNo.trim() in duplicates,
                        readOnly = state.readOnly,
                        onValueChange = { field, value -> viewModel.onValueChange(index, field, value) },
                        onCommit = { field -> viewModel.commit(index, field) },
                        onFocus = { field -> viewModel.onFocus(index, field) },
                        onClear = { viewModel.clearRow(index) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun HeaderText(text: String, modifier: Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun RecordRow(
    record: InboundRecord,
    activeField: ScanField?,
    isDuplicate: Boolean,
    readOnly: Boolean,
    onValueChange: (ScanField, String) -> Unit,
    onCommit: (ScanField) -> Unit,
    onFocus: (ScanField) -> Unit,
    onClear: () -> Unit,
) {
    val background =
        if (activeField != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "${record.seq}",
            modifier = Modifier.width(SEQ_WIDTH),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        ScanTextField(
            value = record.location,
            placeholder = "위치 스캔",
            isActive = activeField == ScanField.LOCATION,
            isError = false,
            readOnly = readOnly,
            onValueChange = { onValueChange(ScanField.LOCATION, it) },
            onCommit = { onCommit(ScanField.LOCATION) },
            onFocused = { onFocus(ScanField.LOCATION) },
            modifier = Modifier.weight(1f),
        )
        ScanTextField(
            value = record.cylinderNo,
            placeholder = "실린더 스캔",
            isActive = activeField == ScanField.CYLINDER,
            isError = isDuplicate,
            readOnly = readOnly,
            onValueChange = { onValueChange(ScanField.CYLINDER, it) },
            onCommit = { onCommit(ScanField.CYLINDER) },
            onFocused = { onFocus(ScanField.CYLINDER) },
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.size(STATUS_WIDTH), contentAlignment = Alignment.Center) {
            when {
                isDuplicate -> Icon(
                    Icons.Filled.Warning,
                    contentDescription = "중복",
                    tint = MaterialTheme.colorScheme.error,
                )
                record.isComplete -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "입력 완료",
                    tint = SuccessGreen,
                )
            }
        }
        if (!readOnly) {
            IconButton(
                onClick = onClear,
                enabled = record.location.isNotEmpty() || record.cylinderNo.isNotEmpty(),
                modifier = Modifier.size(CLEAR_WIDTH),
            ) {
                Icon(Icons.Filled.Clear, contentDescription = "${record.seq}번 지우기")
            }
        }
    }
}

/**
 * 스캐너 입력용 텍스트 필드.
 * 키보드 방식(HID/키보드 웨지) 스캐너는 바코드 값을 입력한 뒤 Enter(또는 Tab)를 보내므로,
 * 그 키를 받으면 [onCommit] 을 호출해 다음 칸으로 넘어갑니다. 손으로 입력 후 Enter 도 동일하게 동작합니다.
 */
@Composable
private fun ScanTextField(
    value: String,
    placeholder: String,
    isActive: Boolean,
    isError: Boolean,
    readOnly: Boolean,
    onValueChange: (String) -> Unit,
    onCommit: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) runCatching { focusRequester.requestFocus() }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = true,
        isError = isError,
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onCommit() },
            onDone = { onCommit() },
        ),
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onFocused() }
            .onPreviewKeyEvent { event ->
                val isTerminator = event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.Tab
                if (isTerminator && !readOnly) {
                    if (event.type == KeyEventType.KeyDown) onCommit()
                    true
                } else {
                    false
                }
            },
    )
}

@Composable
private fun SaveBar(
    completed: Int,
    total: Int,
    duplicateCount: Int,
    canSave: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "입력 완료 $completed / $total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                LinearProgressIndicator(
                    progress = { if (total == 0) 0f else completed.toFloat() / total },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (duplicateCount > 0) {
                    Text(
                        text = "중복된 실린더 번호 ${duplicateCount}건",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Button(
                onClick = onSave,
                enabled = canSave && !isSaving,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 160.dp),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("입고 저장", fontSize = 18.sp)
                }
            }
        }
    }
}

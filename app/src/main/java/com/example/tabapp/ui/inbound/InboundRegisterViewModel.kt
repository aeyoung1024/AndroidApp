package com.example.tabapp.ui.inbound

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabapp.data.inbound.InboundRecord
import com.example.tabapp.data.inbound.InboundRepository
import com.example.tabapp.data.inbound.OrderStatus
import com.example.tabapp.data.inbound.PurchaseOrder
import com.example.tabapp.data.inbound.ScanField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 네비게이션 인자 이름 */
const val ARG_ORDER_NO = "orderNo"

/** 현재 스캔 입력 대상 칸 (몇 번째 레코드의 어떤 항목) */
data class Cell(val row: Int, val field: ScanField)

data class UserMessage(val id: Long, val text: String)

data class InboundRegisterUiState(
    val order: PurchaseOrder? = null,
    val records: List<InboundRecord> = emptyList(),
    val readOnly: Boolean = false,
    val activeCell: Cell? = null,
    val sameLocation: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val message: UserMessage? = null,
) {
    val completedCount: Int get() = records.count { it.isComplete }

    /** 2번 이상 입력된 실린더 번호 */
    val duplicateCylinders: Set<String>
        get() = records.map { it.cylinderNo.trim() }
            .filter { it.isNotEmpty() }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys

    val canSave: Boolean
        get() = !readOnly && records.isNotEmpty() &&
            completedCount == records.size && duplicateCylinders.isEmpty()

    /** 입력한 내용이 있는지 (나가기 전 확인용) */
    val isDirty: Boolean
        get() = !readOnly && !saved && records.any { it.location.isNotBlank() || it.cylinderNo.isNotBlank() }
}

/**
 * 입고 등록 화면 상태.
 * 선택한 발주의 발주량만큼 레코드를 만들고, 스캐너 입력(끝에 Enter)을 받을 때마다
 * 다음 빈 칸으로 자동 이동합니다. (위치 → 실린더 → 다음 레코드 위치 → ...)
 */
class InboundRegisterViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val orderNo: String = checkNotNull(savedStateHandle[ARG_ORDER_NO])
    private var messageSeq = 0L

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<InboundRegisterUiState> = _uiState.asStateFlow()

    private fun createInitialState(): InboundRegisterUiState {
        val order = InboundRepository.getOrder(orderNo)
            ?: return InboundRegisterUiState(readOnly = true)

        val saved = InboundRepository.getInboundRecords(orderNo)
        return if (order.status == OrderStatus.COMPLETED && saved != null) {
            // 입고 완료된 발주는 조회만 가능
            InboundRegisterUiState(order = order, records = saved, readOnly = true)
        } else {
            InboundRegisterUiState(
                order = order,
                records = List(order.quantity) { InboundRecord(seq = it + 1) },
                activeCell = if (order.quantity > 0) Cell(0, ScanField.LOCATION) else null,
            )
        }
    }

    fun onValueChange(row: Int, field: ScanField, raw: String) {
        if (_uiState.value.readOnly) return
        // 스캐너가 Enter/Tab 을 문자로 보내는 경우도 처리
        val hasTerminator = raw.any { it == '\n' || it == '\r' || it == '\t' }
        val value = raw.filterNot { it == '\n' || it == '\r' || it == '\t' }
        updateRecord(row) { it.with(field, value) }
        if (hasTerminator) commit(row, field)
    }

    /** 스캔(또는 Enter) 완료 시 호출: 값 검증 후 다음 칸으로 이동 */
    fun commit(row: Int, field: ScanField) {
        val state = _uiState.value
        if (state.readOnly || row !in state.records.indices) return

        val value = state.records[row].valueOf(field).trim()
        updateRecord(row) { it.with(field, value) }

        if (value.isEmpty()) {
            showMessage("${field.label} 값을 스캔하세요.")
            return
        }

        when (field) {
            ScanField.CYLINDER -> {
                val duplicate = state.records.withIndex()
                    .firstOrNull { (i, r) -> i != row && r.cylinderNo.trim().equals(value, ignoreCase = true) }
                if (duplicate != null) {
                    updateRecord(row) { it.with(field, "") }
                    showMessage("이미 ${duplicate.index + 1}번에 입력된 실린더입니다: $value")
                    return
                }
                if (InboundRepository.isCylinderInbounded(value)) {
                    updateRecord(row) { it.with(field, "") }
                    showMessage("이미 입고된 실린더입니다: $value")
                    return
                }
            }
            ScanField.LOCATION -> {
                if (state.sameLocation) fillEmptyLocations(value)
            }
        }

        moveToNextEmpty(Cell(row, field))
    }

    fun onFocus(row: Int, field: ScanField) {
        if (_uiState.value.readOnly) return
        val cell = Cell(row, field)
        if (_uiState.value.activeCell != cell) _uiState.update { it.copy(activeCell = cell) }
    }

    fun clearRow(row: Int) {
        if (_uiState.value.readOnly) return
        updateRecord(row) { it.copy(location = "", cylinderNo = "") }
        _uiState.update { it.copy(activeCell = Cell(row, ScanField.LOCATION)) }
    }

    /** 켜면 처음 스캔한 입고 위치를 나머지 빈 레코드에 자동으로 채웁니다. */
    fun setSameLocation(enabled: Boolean) {
        _uiState.update { it.copy(sameLocation = enabled) }
        if (!enabled) return
        val state = _uiState.value
        val location = state.records.firstOrNull { it.location.isNotBlank() }?.location ?: return
        fillEmptyLocations(location)
        // 현재 칸이 방금 채워진 위치 칸이면 실린더 칸으로 이동
        val active = state.activeCell
        if (active != null && active.field == ScanField.LOCATION) {
            moveToNextEmpty(active)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaving || state.readOnly) return
        if (!state.canSave) {
            showMessage("모든 레코드를 입력하고 중복된 실린더를 확인하세요.")
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            runCatching {
                InboundRepository.completeInbound(
                    orderNo,
                    state.records.map { it.copy(location = it.location.trim(), cylinderNo = it.cylinderNo.trim()) },
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true, activeCell = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false) }
                showMessage("저장에 실패했습니다: ${e.message}")
            }
        }
    }

    fun consumeMessage(id: Long) {
        _uiState.update { if (it.message?.id == id) it.copy(message = null) else it }
    }

    private fun moveToNextEmpty(from: Cell) {
        val records = _uiState.value.records
        val cells = records.indices.flatMap { r -> ScanField.entries.map { f -> Cell(r, f) } }
        if (cells.isEmpty()) return
        val start = cells.indexOf(from)
        val next = (1..cells.size)
            .map { cells[(start + it).mod(cells.size)] }
            .firstOrNull { records[it.row].valueOf(it.field).isBlank() }

        _uiState.update { it.copy(activeCell = next) }
        if (next == null) showMessage("모든 항목이 입력되었습니다. [입고 저장]을 눌러 주세요.")
    }

    private fun fillEmptyLocations(location: String) {
        _uiState.update { state ->
            state.copy(records = state.records.map { if (it.location.isBlank()) it.copy(location = location) else it })
        }
    }

    private fun updateRecord(row: Int, transform: (InboundRecord) -> InboundRecord) {
        _uiState.update { state ->
            if (row !in state.records.indices) return@update state
            state.copy(records = state.records.toMutableList().also { it[row] = transform(it[row]) })
        }
    }

    private fun showMessage(text: String) {
        _uiState.update { it.copy(message = UserMessage(++messageSeq, text)) }
    }
}

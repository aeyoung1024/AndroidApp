package com.example.tabapp.data.inbound

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * 발주/입고 데이터 저장소.
 * 지금은 샘플 데이터를 메모리에 보관합니다(앱 종료 시 초기화).
 * 추후 서버 API 또는 로컬 DB(Room) 연동으로 교체하세요.
 */
object InboundRepository {

    private val _orders = MutableStateFlow(sampleOrders())
    val orders: StateFlow<List<PurchaseOrder>> = _orders.asStateFlow()

    /** 발주번호 → 입고된 레코드 */
    private val inboundRecords = mutableMapOf(
        "PO-006" to listOf(
            InboundRecord(1, "A-01-01", "CYL-O2-100231"),
            InboundRecord(2, "A-01-01", "CYL-O2-100232"),
            InboundRecord(3, "A-01-02", "CYL-O2-100233"),
        ),
    )

    fun getOrder(orderNo: String): PurchaseOrder? = _orders.value.find { it.orderNo == orderNo }

    fun getInboundRecords(orderNo: String): List<InboundRecord>? = inboundRecords[orderNo]

    /** 이미 다른 발주로 입고된 실린더인지 확인 */
    fun isCylinderInbounded(cylinderNo: String): Boolean =
        inboundRecords.values.any { records -> records.any { it.cylinderNo.equals(cylinderNo, ignoreCase = true) } }

    suspend fun completeInbound(orderNo: String, records: List<InboundRecord>) {
        delay(300) // 서버 전송 흉내
        inboundRecords[orderNo] = records
        _orders.update { list ->
            list.map { if (it.orderNo == orderNo) it.copy(status = OrderStatus.COMPLETED) else it }
        }
    }

    /** 샘플 발주: 조회 기간 확인용으로 오늘 기준 여러 시점의 발주를 생성 */
    private fun sampleOrders(): List<PurchaseOrder> {
        val today = LocalDate.now()
        fun order(
            no: String, supplier: String, code: String, name: String, qty: Int, daysAgo: Long,
            status: OrderStatus = OrderStatus.PENDING,
        ) = PurchaseOrder(
            orderNo = no,
            supplier = supplier,
            itemCode = code,
            itemName = name,
            quantity = qty,
            orderDate = today.minusDays(daysAgo),
            dueDate = today.minusDays(daysAgo).plusDays(7),
            status = status,
        )
        return listOf(
            order("PO-001", "대한가스", "GAS-O2-47", "산소 47L", 10, daysAgo = 2),
            order("PO-002", "한국특수가스", "GAS-N2-47", "질소 47L", 5, daysAgo = 7),
            order("PO-003", "대한가스", "GAS-AR-47", "아르곤 47L", 8, daysAgo = 20),
            order("PO-004", "서울산업가스", "GAS-CO2-20", "이산화탄소 20kg", 4, daysAgo = 50),
            order("PO-005", "한국특수가스", "GAS-HE-47", "헬륨 47L", 3, daysAgo = 120),
            order("PO-006", "서울산업가스", "GAS-O2-47", "산소 47L", 3, daysAgo = 10, status = OrderStatus.COMPLETED),
            order("PO-007", "대한가스", "GAS-N2-47", "질소 47L", 6, daysAgo = 250),
            order("PO-008", "서울산업가스", "GAS-AR-47", "아르곤 47L", 2, daysAgo = 420),
        )
    }
}

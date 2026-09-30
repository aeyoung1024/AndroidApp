package com.example.tabapp.data.inbound

enum class OrderStatus(val label: String) {
    PENDING("미입고"),
    COMPLETED("입고완료"),
}

/** 발주 정보 */
data class PurchaseOrder(
    val orderNo: String,
    val supplier: String,
    val itemCode: String,
    val itemName: String,
    val quantity: Int,
    val orderDate: String,
    val dueDate: String,
    val status: OrderStatus = OrderStatus.PENDING,
)

/** 스캔으로 입력하는 항목 (입력 순서대로 정의) */
enum class ScanField(val label: String) {
    LOCATION("입고 위치"),
    CYLINDER("실린더 번호"),
}

/** 입고 레코드 1건 = 실린더 1개 */
data class InboundRecord(
    val seq: Int,
    val location: String = "",
    val cylinderNo: String = "",
) {
    val isComplete: Boolean get() = location.isNotBlank() && cylinderNo.isNotBlank()

    fun valueOf(field: ScanField): String = when (field) {
        ScanField.LOCATION -> location
        ScanField.CYLINDER -> cylinderNo
    }

    fun with(field: ScanField, value: String): InboundRecord = when (field) {
        ScanField.LOCATION -> copy(location = value)
        ScanField.CYLINDER -> copy(cylinderNo = value)
    }
}

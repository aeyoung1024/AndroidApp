package com.example.tabapp.data

import kotlinx.coroutines.delay

/**
 * 로그인 처리 담당.
 * 지금은 데모용 계정으로 검사하며, 추후 실제 서버 API 호출로 교체하면 됩니다.
 */
object AuthRepository {
    private const val DEMO_ID = "admin"
    private const val DEMO_PASSWORD = "1234"

    suspend fun login(id: String, password: String): Result<String> {
        delay(500) // 네트워크 통신 흉내
        return if (id == DEMO_ID && password == DEMO_PASSWORD) {
            Result.success(id)
        } else {
            Result.failure(IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."))
        }
    }
}

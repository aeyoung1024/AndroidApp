package com.example.tabapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** 현재 화면이 가로 방향인지 여부 (가로폭이 세로폭보다 크면 가로로 판단) */
@Composable
fun isLandscape(): Boolean {
    val config = LocalConfiguration.current
    return config.screenWidthDp > config.screenHeightDp
}

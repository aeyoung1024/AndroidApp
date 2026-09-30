package com.example.tabapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

/**
 * SAIT 로고.
 *
 * 공식 로고 파일을 아래 이름으로 app/src/main/res/drawable/ 에 넣으면 자동으로 이미지 로고를 사용합니다.
 *  - sait_logo.png (또는 .xml / .webp)       : 밝은 배경용 (기본)
 *  - sait_logo_white.png (선택)               : 어두운/파란 배경용
 * 파일이 없으면 텍스트 워드마크로 표시됩니다.
 */
@Composable
fun SaitLogo(
    modifier: Modifier = Modifier,
    height: Dp = 24.dp,
    onDark: Boolean = false,
    color: Color = if (onDark) Color.White else MaterialTheme.colorScheme.primary,
    showSubtitle: Boolean = false,
) {
    val context = LocalContext.current
    val (logoRes, needsTint) = remember(onDark) {
        val white = if (onDark) context.drawableId("sait_logo_white") else 0
        val base = context.drawableId("sait_logo")
        when {
            white != 0 -> white to false
            base != 0 -> base to onDark // 흰색 로고가 없으면 기본 로고를 흰색으로 칠해서 사용
            else -> 0 to false
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        if (logoRes != 0) {
            Image(
                painter = painterResource(logoRes),
                contentDescription = "SAIT",
                colorFilter = if (needsTint) ColorFilter.tint(color) else null,
                modifier = Modifier.height(height),
            )
        } else {
            val fontSize = with(LocalDensity.current) { (height * 0.95f).toSp() }
            Text(
                text = "SAIT",
                color = color,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.14.em,
            )
        }
        if (showSubtitle) {
            Text(
                text = "Samsung Advanced Institute of Technology",
                color = color.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 0.02.em,
            )
        }
    }
}

@SuppressLint("DiscouragedApi")
private fun android.content.Context.drawableId(name: String): Int =
    resources.getIdentifier(name, "drawable", packageName)

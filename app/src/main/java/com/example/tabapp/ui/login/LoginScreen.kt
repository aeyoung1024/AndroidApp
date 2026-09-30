package com.example.tabapp.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabapp.ui.components.LightSystemBarIcons
import com.example.tabapp.ui.components.SaitLogo
import com.example.tabapp.ui.theme.SamsungBlue
import com.example.tabapp.ui.theme.SamsungBlueBright
import com.example.tabapp.ui.theme.SamsungBlueDeep
import com.example.tabapp.ui.theme.TabAppTheme
import com.example.tabapp.ui.theme.isLandscape

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedInUser) {
        state.loggedInUser?.let { user ->
            viewModel.onLoginHandled()
            onLoginSuccess(user)
        }
    }

    LoginContent(
        state = state,
        onIdChange = viewModel::onIdChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLogin = viewModel::login,
    )
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    LightSystemBarIcons()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(SamsungBlueDeep, SamsungBlue, SamsungBlueBright))),
    ) {
        BackgroundDecoration()

        if (isLandscape()) {
            // 가로: 왼쪽 브랜드 문구 / 오른쪽 로그인 카드
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                BrandSection(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 64.dp),
                )
                CenteredScroll(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    LoginCard(state, onIdChange, onPasswordChange, onLogin)
                }
            }
        } else {
            // 세로: 위 브랜드 문구 / 아래 로그인 카드
            CenteredScroll(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                BrandSection(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
                Spacer(Modifier.height(40.dp))
                LoginCard(state, onIdChange, onPasswordChange, onLogin)
            }
        }

        Text(
            text = "v1.0.0",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .safeDrawingPadding()
                .padding(start = 24.dp, bottom = 16.dp),
        )
    }
}

/** 화면이 작거나 키보드가 올라와도 스크롤되며, 공간이 충분하면 가운데 정렬 */
@Composable
private fun CenteredScroll(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}

/** 배경의 은은한 원형 장식 */
@Composable
private fun BackgroundDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = h * 0.75f,
            center = Offset(w * 0.05f, h * 1.05f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = h * 0.45f,
            center = Offset(w * 0.95f, h * -0.05f),
        )
    }
}

@Composable
private fun BrandSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        SaitLogo(height = 36.dp, onDark = true, showSubtitle = true)
        Spacer(Modifier.height(40.dp))
        Text(
            text = "자재관리 시스템",
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Material Management System",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "입고부터 재고 현황까지,\n현장의 모든 자재 흐름을 한 곳에서 관리합니다.",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 26.sp,
        )
    }
}

@Composable
private fun LoginCard(
    state: LoginUiState,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val fieldShape = RoundedCornerShape(14.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
    )

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 24.dp,
        modifier = Modifier
            .widthIn(max = 440.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 36.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("로그인", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "사내 계정 정보를 입력해 주세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.id,
                onValueChange = onIdChange,
                label = { Text("아이디") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                enabled = !state.isLoading,
                shape = fieldShape,
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("비밀번호") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "숨기기" else "보기")
                    }
                },
                singleLine = true,
                enabled = !state.isLoading,
                shape = fieldShape,
                colors = fieldColors,
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    onLogin()
                }),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLogin()
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("로그인", style = MaterialTheme.typography.titleMedium)
                }
            }

            Text(
                text = "데모 계정  admin / 1234",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Preview(name = "가로", widthDp = 1280, heightDp = 800)
@Composable
private fun LoginLandscapePreview() {
    TabAppTheme { LoginContent(LoginUiState(), {}, {}, {}) }
}

@Preview(name = "세로", widthDp = 800, heightDp = 1280)
@Composable
private fun LoginPortraitPreview() {
    TabAppTheme { LoginContent(LoginUiState(), {}, {}, {}) }
}

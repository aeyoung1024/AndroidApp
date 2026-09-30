package com.example.tabapp.ui.login

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabapp.ui.theme.Navy
import com.example.tabapp.ui.theme.NavyDark
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
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (isLandscape()) {
            // 가로: 왼쪽 브랜드 영역 + 오른쪽 로그인 폼
            Row(modifier = Modifier.fillMaxSize()) {
                BrandPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                LoginForm(
                    state = state,
                    onIdChange = onIdChange,
                    onPasswordChange = onPasswordChange,
                    onLogin = onLogin,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .systemBarsPadding()
                        .imePadding(),
                )
            }
        } else {
            // 세로: 위쪽 브랜드 영역 + 아래 로그인 폼
            Column(modifier = Modifier.fillMaxSize()) {
                BrandPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f),
                )
                LoginForm(
                    state = state,
                    onIdChange = onIdChange,
                    onPasswordChange = onPasswordChange,
                    onLogin = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.65f)
                        .systemBarsPadding()
                        .imePadding(),
                )
            }
        }
    }
}

@Composable
private fun BrandPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Brush.linearGradient(listOf(Navy, NavyDark))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(72.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text("TabApp", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("업무용 태블릿 애플리케이션", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
        }
    }
}

@Composable
private fun LoginForm(
    state: LoginUiState,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // 키보드가 올라와 공간이 좁아져도 스크롤로 모든 입력란에 접근 가능
    BoxWithConstraints(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "계정 정보를 입력하세요.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = state.id,
                    onValueChange = onIdChange,
                    label = { Text("아이디") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    enabled = !state.isLoading,
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
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onLogin()
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("로그인", fontSize = 18.sp)
                    }
                }

                Text(
                    text = "데모 계정: admin / 1234",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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

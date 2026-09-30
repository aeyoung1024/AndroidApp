package com.example.tabapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tabapp.data.Feature
import com.example.tabapp.ui.feature.FeatureScreen
import com.example.tabapp.ui.login.LoginScreen
import com.example.tabapp.ui.main.MainScreen

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // 회전 등 구성 변경 시에도 로그인 사용자 이름 유지
    var userName by rememberSaveable { mutableStateOf("") }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    userName = user
                    navController.navigate(Routes.MAIN) {
                        // 로그인 후 뒤로가기로 로그인 화면에 돌아가지 않도록 제거
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                userName = userName,
                onFeatureClick = { feature ->
                    navController.navigate(feature.route) { launchSingleTop = true }
                },
                onLogout = {
                    userName = ""
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }

        Feature.entries.forEach { feature ->
            composable(feature.route) {
                FeatureScreen(
                    feature = feature,
                    onBack = {
                        // 뒤로가기 버튼 연타 시 메인 화면까지 닫히지 않도록 현재 화면일 때만 처리
                        if (navController.currentBackStackEntry?.destination?.route == feature.route) {
                            navController.popBackStack()
                        }
                    },
                )
            }
        }
    }
}

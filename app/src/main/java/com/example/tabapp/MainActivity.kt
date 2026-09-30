package com.example.tabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tabapp.navigation.AppNavHost
import com.example.tabapp.ui.theme.TabAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabAppTheme {
                AppNavHost()
            }
        }
    }
}

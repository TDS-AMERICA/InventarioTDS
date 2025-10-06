package com.example.inventariotds
/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.inventariotds.ui.theme.AppNavigation
import com.example.inventariotds.ui.theme.InventarioTDSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioTDSTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

 */


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.inventariotds.ui.theme.InventarioTDSTheme
import com.example.inventariotds.ui.theme.MainWrapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioTDSTheme {
                MainWrapper(applicationContext)
            }
        }
    }
}

package com.example.goberpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
// Import đúng tên hàm từ file của cậu
import com.example.goberpro.screen.BarberMainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Gọi đúng tên hàm đã khai báo bên file kia
                BarberMainScreen()
            }
        }
    }
}
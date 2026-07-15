package com.example.goberpro.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text("← Cá Nhân", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Avatar tròn
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFD9D9D9)), // Màu xám sáng cho Avatar Placeholder
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = "Avatar", modifier = Modifier.size(60.dp), tint = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Tên Người Dùng", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // Các mục thông tin
        ProfileMenuItem("Chi Tiết Thông Tin")
        ProfileMenuItem("Hạng Thành Viên", "Thành viên Vàng")
        ProfileMenuItem("Ví GoBer", "0 Đ")

        Spacer(modifier = Modifier.weight(1f)) // Đẩy phần liên hệ xuống dưới cùng

        // Liên hệ CSKH
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Liên Hệ Chăm Sóc Khách Hàng", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Liên Hệ Quan Hotline : 0788516177", color = AccentGold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileMenuItem(title: String, value: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (value.isNotEmpty()) {
            Text(value, color = AccentGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
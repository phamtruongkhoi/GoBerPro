package com.example.goberpro.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Import ViewModel
import com.example.goberpro.viewmodel.BarberViewModel

// -- BẢNG MÀU --
val BackgroundColor = Color(0xFF121212)
val SurfaceColor = Color(0xFF1E1E1E)
val AccentGold = Color(0xFFD4AF37)
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFA0A0A0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberMainScreen(viewModel: BarberViewModel = viewModel()) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var showInvoice by remember { mutableStateOf(false) }
    var showConfirmScreen by remember { mutableStateOf(false) }

    val items = listOf("Trang Chủ", "Đặt Lịch", "Lịch Sử", "Thông Báo", "Cá Nhân")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.AutoMirrored.Filled.List, Icons.Filled.Notifications, Icons.Filled.Person)

    Scaffold(
        bottomBar = {
            // Ẩn thanh điều hướng khi đang ở màn hình xác nhận hoặc hóa đơn
            if (!showConfirmScreen && !showInvoice) {
                NavigationBar(
                    containerColor = BackgroundColor,
                    contentColor = TextPrimary
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item, fontSize = 10.sp) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentGold,
                                selectedTextColor = AccentGold,
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.padding(if (showConfirmScreen || showInvoice) PaddingValues(0.dp) else paddingValues).fillMaxSize()) {
            when (selectedItem) {
                0 -> HomeScreen() // Gọi màn hình mới ở đây!
                1 -> {
                    if (showInvoice) {
                        InvoiceScreen(
                            viewModel = viewModel,
                            onNavigateToHome = {
                                showInvoice = false
                                showConfirmScreen = false
                                selectedItem = 0
                            }
                        )
                    } else if (showConfirmScreen) {
                        // Đã fix lỗi: Bỏ các tham số thừa, chỉ truyền viewModel và onBack
                        ConfirmBookingScreen(
                            viewModel = viewModel,
                            onBack = { showConfirmScreen = false }
                        )

                        // Lắng nghe khi đặt lịch thành công để hiện hóa đơn
                        val bookingSuccess by viewModel.bookingSuccess.collectAsState()
                        LaunchedEffect(bookingSuccess) {
                            if (bookingSuccess) {
                                showInvoice = true
                                showConfirmScreen = false
                            }
                        }
                    } else {
                        BookingScreen(
                            viewModel = viewModel,
                            onBookingConfirmed = { _, _, _, _ ->
                                showConfirmScreen = true
                            }
                        )
                    }
                }
                2 -> HistoryScreen(
                    // Đã fix lỗi: Thêm onEditBooking để HistoryScreen quay lại được màn đặt lịch
                    viewModel = viewModel,
                    onEditBooking = {
                        selectedItem = 1
                        showInvoice = false
                        showConfirmScreen = false
                    }
                )
                // THAY THẾ 2 DÒNG PLACEHOLDER CŨ BẰNG 2 DÒNG NÀY:
                3 -> NotificationScreen()
                4 -> ProfileScreen()
            }
        }
    }
}

// Hàm hiển thị tạm cho các tab chưa có giao diện
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = AccentGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
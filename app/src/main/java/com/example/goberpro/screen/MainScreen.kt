package com.example.goberpro.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// Hỗ trợ Supabase
import io.github.jan.supabase.postgrest.postgrest
import com.example.goberpro.supabase

// Import Model và ViewModel (Theo kiến trúc của bạn cậu)
import com.example.goberpro.model.BarberService
import com.example.goberpro.model.Booking
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

    // Lưu tạm thông tin khách hàng để truyền giữa các màn hình
    var tempCustomerName by remember { mutableStateOf("") }
    var tempPhoneNumber by remember { mutableStateOf("") }
    var tempDate by remember { mutableStateOf("") }
    var tempTime by remember { mutableStateOf("") }

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
                0 -> HomeContent() // Giao diện trang chủ đã gộp code ảnh của cậu
                1 -> {
                    // Luồng xử lý đặt lịch của bạn cậu
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
                        ConfirmBookingScreen(
                            viewModel = viewModel,
                            customerName = tempCustomerName,
                            phoneNumber = tempPhoneNumber,
                            selectedDate = tempDate,
                            selectedTime = tempTime,
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
                            onBookingConfirmed = { name, phone, date, time ->
                                tempCustomerName = name
                                tempPhoneNumber = phone
                                tempDate = date
                                tempTime = time
                                showConfirmScreen = true
                            }
                        )
                    }
                }
                2 -> HistoryScreen(viewModel = viewModel)
                3 -> PlaceholderScreen("Màn Hình Thông Báo")
                4 -> PlaceholderScreen("Màn Hình Cá Nhân")
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

// Toàn bộ giao diện trang chủ đã được cấu hình tự động gọi API của Supabase
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent() {
    var serviceList by remember { mutableStateOf<List<BarberService>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val data = supabase.postgrest["services"].select().decodeList<BarberService>()
            serviceList = data
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Lỗi kết nối Supabase"
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 1. Thanh tìm kiếm
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tìm Dịch Vụ Bạn Muốn Làm", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AccentGold
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. ComBo Ưu Đãi
        item {
            Text("ComBo Ưu Đãi", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(3) {
                    Card(
                        modifier = Modifier.size(width = 240.dp, height = 140.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Info, contentDescription = "Image Placeholder", tint = TextSecondary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Gober (Thợ cắt tóc)
        item {
            Text("Gober", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(4) {
                    Card(
                        modifier = Modifier.size(width = 120.dp, height = 160.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Face, contentDescription = "Barber", tint = TextSecondary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. Dịch Vụ Nổi Bật - Đổ dữ liệu thật kèm ảnh
        item {
            Text("Dịch Vụ Nổi Bật", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            isLoading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentGold)
                    }
                }
            }
            errorMessage != null -> {
                item {
                    Text(text = "Đã xảy ra lỗi: $errorMessage", color = Color.Red, modifier = Modifier.padding(8.dp))
                }
            }
            serviceList.isEmpty() -> {
                item {
                    Text(text = "Không có dịch vụ nào tồn tại.", color = TextSecondary, modifier = Modifier.padding(8.dp))
                }
            }
            else -> {
                items(serviceList.size) { index ->
                    val service = serviceList[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // HIỂN THỊ ẢNH THẬT BẰNG ASYNCIMAGE (Code của cậu)
                        AsyncImage(
                            model = service.imageUrl,
                            contentDescription = service.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = service.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Fix luôn cảnh báo Locale ở đây
                            Text(text = "${String.format(java.util.Locale.US, "%,d", service.price)} Đ", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
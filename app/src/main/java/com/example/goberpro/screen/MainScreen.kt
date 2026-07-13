package com.example.goberpro.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.serialization.SerialName


// Các import hỗ trợ Supabase và trạng thái Loading
import androidx.compose.material3.CircularProgressIndicator
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.example.goberpro.supabase // Lưu ý: Đổi tên package nếu file SupabaseClient của cậu nằm ở thư mục khác

// -- BẢNG MÀU --
val BackgroundColor = Color(0xFF121212)
val SurfaceColor = Color(0xFF1E1E1E)
val AccentGold = Color(0xFFD4AF37)
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFA0A0A0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberMainScreen() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Trang Chủ", "Đặt Lịch", "Lịch Sử", "Thông Báo", "Cá Nhân")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.List, Icons.Filled.Notifications, Icons.Filled.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundColor,
                contentColor = TextPrimary
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item, fontSize = 10.sp) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentGold,
                            selectedTextColor = AccentGold,
                            indicatorColor = Color.Transparent, // Tắt màu nền tròn khi chọn
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        // BẮT ĐẦU PHẦN XỬ LÝ CHUYỂN TAB Ở ĐÂY
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedItem) {
                0 -> HomeContent() // Ruột trang chủ đã được cập nhật Supabase
                1 -> PlaceholderScreen("Màn Hình Đặt Lịch")
                2 -> PlaceholderScreen("Màn Hình Lịch Sử")
                3 -> PlaceholderScreen("Màn Hình Thông Báo")
                4 -> PlaceholderScreen("Màn Hình Cá Nhân")
            }
        }
    }
}

// Hàm tạm thời để hiển thị chữ cho các tab chưa có giao diện
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = AccentGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// 1. Khai báo khuôn mẫu dữ liệu khớp với bảng trên Supabase
@Serializable
data class BarberService(
    val id: Long,
    val name: String,
    val price: Long,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null // Cột chứa link ảnh từ Supabase
)

// Toàn bộ giao diện trang chủ đã được cấu hình tự động gọi API của Supabase
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent() {
    // 2. Tạo trạng thái lưu danh sách dịch vụ và trạng thái loading
    var serviceList by remember { mutableStateOf<List<BarberService>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 3. LaunchedEffect dùng để tự động fetch dữ liệu từ Supabase khi mở màn hình
    LaunchedEffect(Unit) {
        try {
            // Gọi xuống Supabase lấy toàn bộ bảng "services" và ép kiểu sang List<BarberService>
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

        // [GIỮ NGUYÊN] 1. Thanh tìm kiếm
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

        // [GIỮ NGUYÊN] 2. ComBo Ưu Đãi
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

        // [GIỮ NGUYÊN] 3. Gober (Thợ cắt tóc)
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

        // 4. Dịch Vụ Nổi Bật - NƠI ĐỔ DỮ LIỆU THẬT SUPABASE
        item {
            Text("Dịch Vụ Nổi Bật", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Kiểm tra các trạng thái khi tải dữ liệu
        when {
            isLoading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentGold) // Vòng xoay tải dữ liệu màu Gold sang trọng
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
                // ĐÃ CÓ DỮ LIỆU THẬT: Duyệt qua danh sách lấy từ Supabase
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
                        // HIỂN THỊ ẢNH THẬT BẰNG ASYNCIMAGE
                        AsyncImage(
                            model = service.imageUrl, // Link ảnh lấy từ Supabase
                            contentDescription = service.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)), // Bo góc ảnh cho đẹp
                            contentScale = ContentScale.Crop // Cắt ảnh vừa vặn với khung vuông
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Cột thông tin chữ giữ nguyên
                        Column {
                            Text(text = service.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Định dạng hiển thị tiền tệ cơ bản
                            Text(text = "${String.format("%,d", service.price)} Đ", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
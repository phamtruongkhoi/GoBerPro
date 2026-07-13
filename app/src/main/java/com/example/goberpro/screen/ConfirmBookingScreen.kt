package com.example.goberpro.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goberpro.viewmodel.BarberViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookingScreen(
    viewModel: BarberViewModel,
    customerName: String,
    phoneNumber: String,
    selectedDate: String,
    selectedTime: String,
    onBack: () -> Unit
) {
    val selectedServices by viewModel.selectedServices.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val discountAmount by viewModel.discountAmount.collectAsState()
    val finalPrice by viewModel.finalPrice.collectAsState()
    val appliedDiscount by viewModel.appliedDiscount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var discountCodeInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Lịch Hẹn Của Bạn", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Phần Header Tiệm
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.DarkGray
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Gobar's Barber Shop", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Text("490 Lý Thái Tổ, Quận 10", color = TextSecondary, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(14.dp))
                            Text("4.5 (24)", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Nội dung chi tiết
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = BackgroundColor, 
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Ngày & Giờ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ngày & Giờ", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Text(
                        "$selectedTime Ngày $selectedDate",
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Danh sách dịch vụ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.List, null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Danh sách dịch vụ", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    selectedServices.forEach { service ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = SurfaceColor) {}
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(service.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(service.description ?: "", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Text(
                                text = "${String.format(Locale.getDefault(), "%,d", service.price)} Đ",
                                color = AccentGold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Ưu đãi
                    Text("Ưu đãi/ Mã giảm giá", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = AccentGold, modifier = Modifier.padding(8.dp))
                        TextField(
                            value = discountCodeInput,
                            onValueChange = { discountCodeInput = it.uppercase() },
                            placeholder = { Text("Nhập mã", color = TextSecondary, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = { viewModel.applyDiscountCode(discountCodeInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            enabled = discountCodeInput.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Áp dụng", fontSize = 12.sp)
                            }
                        }
                    }
                    
                    if (appliedDiscount != null) {
                        Text(
                            "Đã áp dụng mã: ${appliedDiscount?.code} (Giảm ${appliedDiscount?.discount_percent}%)",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (errorMessage != null && discountCodeInput.isNotBlank()) {
                         Text(
                            errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Chi tiết đơn hàng
                    Text("Chi tiết đơn hàng", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    selectedServices.forEach { service ->
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text(service.name, color = TextSecondary)
                            Text("${String.format(Locale.getDefault(), "%,d", service.price)} Đ", color = TextPrimary)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Giảm giá", color = TextSecondary)
                        Text("-${String.format(Locale.getDefault(), "%,d", discountAmount)} Đ", color = Color.Red)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.DarkGray)
                    
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tổng tiền", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "${String.format(Locale.getDefault(), "%,d", if (appliedDiscount != null) finalPrice else totalPrice)} Đ",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = AccentGold
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    // Nút xác nhận
                    Button(
                        onClick = {
                            viewModel.confirmBooking(customerName, phoneNumber, selectedDate, selectedTime)
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text("ĐẶT LỊCH NGAY", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

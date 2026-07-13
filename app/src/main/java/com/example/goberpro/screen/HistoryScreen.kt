package com.example.goberpro.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goberpro.model.Booking
import com.example.goberpro.viewmodel.BarberViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: BarberViewModel) {
    val bookingHistory by viewModel.bookingHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBookingHistory()
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lịch Sử Đặt Lịch", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundColor)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading && bookingHistory.isEmpty()) {
                CircularProgressIndicator(
                    color = AccentGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (bookingHistory.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Info, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Chưa có lịch sử đặt hẹn", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookingHistory) { booking ->
                        HistoryItem(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn #${booking.id ?: "..."}",
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Surface(
                    color = when(booking.status) {
                        "Paid" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        "Pending" -> Color(0xFFD4AF37).copy(alpha = 0.2f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = when(booking.status) {
                            "Paid" -> Color(0xFF4CAF50)
                            "Pending" -> AccentGold
                            else -> Color.White
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = AccentGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = booking.booking_date, color = TextPrimary, fontSize = 14.sp)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.DateRange, null, tint = AccentGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = booking.booking_time, color = TextPrimary, fontSize = 14.sp)
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Khách hàng: ${booking.customer_name}",
                color = TextSecondary,
                fontSize = 14.sp
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tổng thanh toán", color = TextPrimary, fontWeight = FontWeight.Medium)
                Text(
                    text = "${String.format(Locale.getDefault(), "%,d", booking.total_price)} Đ",
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

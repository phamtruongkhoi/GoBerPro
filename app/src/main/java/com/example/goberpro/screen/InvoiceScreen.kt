package com.example.goberpro.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goberpro.util.PdfGenerator
import com.example.goberpro.viewmodel.BarberViewModel
import java.util.Locale

@Composable
fun InvoiceScreen(
    viewModel: BarberViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeBooking by viewModel.activeBooking.collectAsState()
    val selectedServices by viewModel.selectedServices.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Scaffold(containerColor = BackgroundColor, modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(Modifier.height(20.dp))
                Icon(Icons.Default.CheckCircle, "Success", tint = Color(0xFF009688), modifier = Modifier.size(72.dp))
                Text("ĐẶT LỊCH THÀNH CÔNG!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "GOBER BARBER SHOP",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = AccentGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(color = Color.DarkGray)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Khách hàng:", color = Color.Gray)
                            Text(activeBooking?.customer_name ?: "", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Ngày hẹn:", color = Color.Gray)
                            Text("${activeBooking?.booking_date} | ${activeBooking?.booking_time}", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Trạng thái:", color = Color.Gray)
                            Box(
                                Modifier
                                    .background(Color(0xFF009688).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("CHƯA THANH TOÁN", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF009688))
                            }
                        }
                        HorizontalDivider(color = Color.DarkGray)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Tổng tiền:", color = Color.Gray)
                            Text(
                                "${String.format(Locale.getDefault(), "%,d", totalPrice)} Đ",
                                fontWeight = FontWeight.Bold,
                                color = AccentGold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { activeBooking?.let { PdfGenerator.generateInvoicePdf(context, it, selectedServices) } },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("TẢI HÓA ĐƠN (PDF)", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.resetBookingSelection(); onNavigateToHome() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    border = BorderStroke(1.dp, AccentGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("QUAY LẠI TRANG CHỦ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoiceScreenPreview() {
    MaterialTheme {
        // Mock data for preview if needed, or just a simple placeholder
        Text("Invoice Preview")
    }
}